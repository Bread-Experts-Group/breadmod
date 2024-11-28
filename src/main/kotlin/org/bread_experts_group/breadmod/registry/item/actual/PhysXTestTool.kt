package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.util.renderBuffer
import org.bread_experts_group.breadmod.util.rgMinecraft
import physx.PxTopLevelFunctions
import physx.common.*
import physx.geometry.PxBoxGeometry
import physx.physics.*
import physx.support.PxPvd
import physx.support.PxPvdInstrumentationFlagEnum
import physx.support.PxPvdInstrumentationFlags
import physx.support.PxPvdTransport
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import kotlin.io.path.*

class PhysXTestTool : Item(Properties().stacksTo(1)) {

    val logger: Logger = LogManager.getLogger("PhysX Test Tool")

    private val classLoader: ClassLoader = run {
        val loader = this::class.java.classLoader
            ?: throw IllegalStateException("Class loader is null")
        val librariesPath = Path(System.getProperty("java.io.tmpdir"))
        System.setProperty("physxjni.nativeLibLocation", librariesPath.absolutePathString())
        System.setProperty("physxjni.loadFromResources", "false")
        Files.createDirectories(librariesPath)
        val osName = System.getProperty("os.name", "unknown").lowercase()
        val arch = System.getProperty("os.arch", "unknown").lowercase()

        object : ClassLoader("PhysX ClassLoader", Thread.currentThread().contextClassLoader) {
            val loadMap: MutableMap<String, ByteArray> = mutableMapOf()

            fun loadResourceJAR(name: String) {
                loadJAR(
                    loader.getResource("/libraries/physx-jni-$name.jar")
                        ?: throw IllegalStateException("Can't find JAR for $name")
                )
            }

            fun loadJAR(url: URL) {
                val jar = JarInputStream(url.openConnection().getInputStream())
                var entry: JarEntry? = jar.nextJarEntry
                while (entry != null) {
                    entry.let {
                        try {
                            if (it.name.endsWith(".class", true)) {
                                val load = it.name.substringBeforeLast('.').replace('/', '.')
                                loadMap[load] = jar.readBytes()
                            } else if (it.name.matches(Regex("^.+\\.(so|dylib|dll)$"))) {
                                val libPath = librariesPath.resolve(it.name.substringAfterLast('/'))
                                if (!libPath.exists()) libPath.writeBytes(jar.readBytes())
                            }
                            null
                        } catch (e: Throwable) {
                            logger.warn("Failed to load JAR object ${it.name}", e)
                        }
                    }
                    entry = jar.nextJarEntry
                }
            }

            init {
                logger.info("Loading PhysX JARs [Core]...")
                loadResourceJAR("2.4.2")
                logger.info("Loading PhysX JARs [Native]...")
                when {
                    osName.contains("windows") -> loadResourceJAR("2.4.2-natives-windows")
                    osName.contains("linux") -> loadResourceJAR("2.4.2-natives-linux")
                    else -> {
                        if (osName.contains("mac os x") || osName.contains("darwin") || osName.contains("osx")) {
                            if (arch == "aarch64") loadResourceJAR("2.4.2-natives-macos-arm64")
                            else loadResourceJAR("2.4.2-natives-macos")
                        } else throw IllegalStateException("Bad platform: $osName, $arch")
                    }
                }
                logger.info("PhysX JARs loaded.")
                logger.info("Loading self-reference...")
                val path = File(
                    this::class.java.protectionDomain.codeSource.location
                        .toURI().path
                        .substringBeforeLast('#')
                ).toPath()
                if (path.isRegularFile()) loadJAR(path.toUri().toURL())
                else {
                    Files.walk(path.parent.parent).forEach {
                        if (!(it.isRegularFile() && it.name.endsWith(".class"))) return@forEach
                        val className =
                            it.absolutePathString()
                                .substringAfter("main")
                                .substring(1)
                                .replace(File.separatorChar, '.')
                                .substringBeforeLast(".class")
                        loadMap[className] = it.readBytes()
                    }
                }
                logger.info("Self-references loaded, PhysX ClassLoader ready.")
            }

            override fun loadClass(name: String, resolve: Boolean): Class<*> {
                findLoadedClass(name)?.let { return it }
                val clazz = if (
                    name.startsWith("de.fabmax.physxjni.") ||
                    name.startsWith("physx.") ||
                    name.contains("PhysX", false)
                ) {
                    synchronized(getClassLoadingLock(name)) {
                        loadMap.remove(name)?.let {
                            defineClass(name, it, 0, it.size)
                        } ?: throw ClassNotFoundException("Class $name not found")
                    }
                } else {
                    this.parent.loadClass(name)
                }

                if (resolve) resolveClass(clazz)
                return clazz
            }
        }
    }

    private val physX by lazy {
        Class.forName("org.bread_experts_group.breadmod.registry.item.actual.PhysXTestTool\$PhysX", true, classLoader)
            .getDeclaredConstructor()
            .newInstance()
    }

    class PhysX {
        private val version = PxTopLevelFunctions.getPHYSICS_VERSION()

        private val allocator = PxDefaultAllocator()
        private val errorCb = PxDefaultErrorCallback()
        private val foundation: PxFoundation = PxTopLevelFunctions.CreateFoundation(version, allocator, errorCb)

        private val pvd: PxPvd = PxTopLevelFunctions.CreatePvd(foundation)
        private val transport: PxPvdTransport = PxTopLevelFunctions.DefaultPvdSocketTransportCreate(
            "localhost", 5425,
            10000
        )

        private val tolerances = PxTolerancesScale()
        private val physics: PxPhysics = PxTopLevelFunctions.CreatePhysics(version, foundation, tolerances, pvd)

        private val numThreads = Runtime.getRuntime().availableProcessors()
        private val cpuDispatcher: PxDefaultCpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(numThreads)

        private val sceneDescription = PxSceneDesc(tolerances)
        private val scene: PxScene

        private val defaultMaterial = physics.createMaterial(0.5f, 0.5f, 0.5f)

        private val tmpPose = PxTransform(PxIDENTITYEnum.PxIdentity)
        private val tmpFilterData = PxFilterData(1, 1, 0, 0)
        private val shapeFlags = PxShapeFlags(
            (PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value or PxShapeFlagEnum.eSIMULATION_SHAPE.value).toByte()
        )

        private var noExecute = true
//        private fun suspendSimulation() {
//            noExecute = true
//            scene.fetchResults(true)
//        }

        private fun resumeSimulation() {
            noExecute = false
        }

        private val rigidActors: MutableList<PxRigidActor> = mutableListOf()

        init {
            pvd.connect(transport, PxPvdInstrumentationFlags(PxPvdInstrumentationFlagEnum.eALL.value.toByte()))
            sceneDescription.gravity = PxVec3(0f, -9.807f, 0f)
            sceneDescription.cpuDispatcher = cpuDispatcher
            sceneDescription.filterShader = PxTopLevelFunctions.DefaultFilterShader()
            scene = physics.createScene(sceneDescription)

            // create a large static box with size 20x1x20 as ground
            val groundGeometry = PxBoxGeometry(10f, 0.5f, 10f) // PxBoxGeometry uses half-sizes
            val groundShape = physics.createShape(groundGeometry, defaultMaterial, true, shapeFlags)
            val ground = physics.createRigidStatic(tmpPose)
            groundShape.simulationFilterData = tmpFilterData
            ground.attachShape(groundShape)
            scene.addActor(ground)
            renderBuffer.add(mutableListOf<Float>() to { _, event ->
                if (!noExecute) {
                    scene.simulate(1f / 60f)
                    scene.fetchResults(true)
                }

                rigidActors.forEach { actor ->
                    val level = rgMinecraft.level ?: return@forEach
                    val p = actor.globalPose.p
                    level.addParticle(
                        ParticleTypes.SMOKE,
                        p.x.toDouble(), p.y.toDouble(), p.z.toDouble(),
                        0.0, 0.0, 0.0
                    )
                }

                false
            })
            resumeSimulation()
        }

        fun addCube() {
            println("Cube added")
            // create a small dynamic box with size 1x1x1, which will fall on the ground
            tmpPose.p = PxVec3(0f, 5f, 0f)
            val boxGeometry = PxBoxGeometry(0.5f, 0.5f, 0.5f) // PxBoxGeometry uses half-sizes
            val boxShape = physics.createShape(boxGeometry, defaultMaterial, true, shapeFlags)
            val box = physics.createRigidDynamic(tmpPose)
            boxShape.simulationFilterData = tmpFilterData
            box.attachShape(boxShape)
            scene.addActor(box)
            rigidActors.add(box)

            // clean up temp objects
//        groundGeometry.destroy()
//        boxGeometry.destroy()
//        tmpFilterData.destroy()
//        tmpPose.destroy()
//        tmpVec.destroy()
//        shapeFlags.destroy()
//        sceneDesc.destroy()
//        tolerances.destroy()

            // cleanup stuff
//        scene.removeActor(ground)
//        ground.release()
//        groundShape.release()
//
//        scene.removeActor(box)
//        box.release()
//        boxShape.release()
//
//        scene.release()
//        material.release()
//        physics.release()
//        pvd.release()
//        transport.release()
//        foundation.release()
//        errorCb.destroy()
//        allocator.destroy()
        }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level is ServerLevel) return InteractionResultHolder.pass(player.getItemInHand(usedHand))
        Thread.currentThread().contextClassLoader = classLoader

        try {
            physX::class.java.getDeclaredMethod("addCube").invoke(physX)
        } catch (e: Throwable) {
            player.sendSystemMessage(Component.literal("PhysX failed to load: ${e.message}"))
            logger.error("PhysX failed to load", e)
        }

        return super.use(level, player, usedHand)
    }
}