package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.item.IRegisterSpecialCreativeTab
import org.bread_experts_group.breadmod.registry.menu.ModCreativeTabs
import org.bread_experts_group.breadmod.util.render.RenderBuffer
import org.bread_experts_group.breadmod.util.render.initialTranslate
import org.bread_experts_group.breadmod.util.render.localClient
import org.bread_experts_group.breadmod.util.render.renderBlockModel
import org.joml.Quaternionf
import physx.PxTopLevelFunctions
import physx.common.PxDefaultAllocator
import physx.common.PxDefaultCpuDispatcher
import physx.common.PxErrorCallback
import physx.common.PxErrorCodeEnum
import physx.common.PxFoundation
import physx.common.PxIDENTITYEnum
import physx.common.PxTolerancesScale
import physx.common.PxTransform
import physx.common.PxVec3
import physx.geometry.PxBoxGeometry
import physx.physics.PxFilterData
import physx.physics.PxMaterial
import physx.physics.PxPhysics
import physx.physics.PxRigidActor
import physx.physics.PxScene
import physx.physics.PxSceneDesc
import physx.physics.PxShapeFlagEnum
import physx.physics.PxShapeFlags
import physx.support.PxOmniPvd
import java.io.File
import java.net.URL
import java.nio.file.Files
import java.util.function.Supplier
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import kotlin.io.path.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.isRegularFile
import kotlin.io.path.name
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

internal object PhysXTestTool : Item(Properties().stacksTo(1)), IRegisterSpecialCreativeTab {
	val logger: Logger = LogManager.getLogger("PhysX Test Tool")
	private val classLoader: ClassLoader = this.run {
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
				this.loadJAR(
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
								this.loadMap[load] = jar.readBytes()
							} else if (it.name.matches(Regex("^.+\\.(so|dylib|dll)$"))) {
								val libPath = librariesPath.resolve(it.name.substringAfterLast('/'))
								if (!libPath.exists()) libPath.writeBytes(jar.readBytes())
							}
							null
						} catch (e: Throwable) {
							this@run.logger.warn("Failed to load JAR object ${it.name}", e)
						}
					}
					entry = jar.nextJarEntry
				}
			}

			init {
				this@run.logger.info("Loading PhysX JARs [Core]...")
				this.loadResourceJAR("2.5.0")
				this@run.logger.info("Loading PhysX JARs [Native]...")
				when {
					osName.contains("windows") -> this.loadResourceJAR("natives-windows-2.5.0")
					osName.contains("linux")   -> this.loadResourceJAR("natives-linux-2.5.0")
					else                       -> {
						if (osName.contains("mac os x") || osName.contains("darwin") || osName.contains("osx")) {
							if (arch == "aarch64") this.loadResourceJAR("natives-macos-arm64-2.5.0")
							else this.loadResourceJAR("natives-macos-2.5.0")
						} else throw IllegalStateException("Bad platform: $osName, $arch")
					}
				}
				this@run.logger.info("PhysX JARs loaded.")
				this@run.logger.info("Loading self-reference...")
				val path = File(
					this::class.java.protectionDomain.codeSource.location
						.toURI().path
						.substringBeforeLast('#')
				).toPath()
				if (path.isRegularFile()) this.loadJAR(path.toUri().toURL())
				else {
					Files.walk(path.parent.parent).forEach {
						if (!(it.isRegularFile() && it.name.endsWith(".class"))) return@forEach
						val className =
							it.absolutePathString()
								.substringAfter("main")
								.substring(1)
								.replace(File.separatorChar, '.')
								.substringBeforeLast(".class")
						this.loadMap[className] = it.readBytes()
					}
				}
				this@run.logger.info("Self-references loaded, PhysX ClassLoader ready.")
			}

			override fun loadClass(name: String, resolve: Boolean): Class<*> {
				this.findLoadedClass(name)?.let { return it }
				val clazz = if (
					name.startsWith("de.fabmax.physxjni.") ||
					name.startsWith("physx.") ||
					name.contains("PhysX", false)
				) {
					synchronized(this.getClassLoadingLock(name)) {
						this.loadMap.remove(name)?.let {
							this.defineClass(name, it, 0, it.size)
						} ?: throw ClassNotFoundException("Class $name not found")
					}
				} else {
					this.parent.loadClass(name)
				}

				if (resolve) this.resolveClass(clazz)
				return clazz
			}
		}
	}
	private var physX: Any = 0
	fun createPhysX() {
		if (this.physX == 0) this.physX = Class.forName(
			"org.bread_experts_group.breadmod.registry.item.actual.PhysXTestTool\$PhysX",
			true,
			this.classLoader
		).getDeclaredConstructor().newInstance()
	}

	fun destroyPhysX() {
		if (this.physX != 0) {
			this.physX::class.java.getDeclaredMethod("cleanup").invoke(this.physX)
			this.physX = 0
		}
	}

	@Suppress("unused")
	class PhysX {
		private val logger = LogManager.getLogger()

		// PhysX System Objects
		private val allocator: PxDefaultAllocator = PxDefaultAllocator()
		private val foundation: PxFoundation
		private val tolerances: PxTolerancesScale = PxTolerancesScale()
		private val physics: PxPhysics
		private val cpuDispatcher: PxDefaultCpuDispatcher
		private val sceneDescription = PxSceneDesc(this.tolerances)
		private val scene: PxScene
		private val errorHandler = object : PxErrorCallback() {
			override fun reportError(code: PxErrorCodeEnum, message: String, file: String, line: Int) {
				super.reportError(code, message, file, line)
				this@PhysXTestTool.logger.error("PhysX Error: $code, $message, $file, $line")
			}
		}

		// PhysX Configuration
		private var noExecute = true

		private fun suspendSimulation() {
			this.noExecute = true
			this.scene.fetchResults(true)
		}

		private fun resumeSimulation() {
			this.noExecute = false
		}

		// PhysX Objects
		private val rigidActors: MutableList<PxRigidActor> = mutableListOf()
		private val materials: MutableMap<String, PxMaterial> = mutableMapOf()

		fun defineMaterial(
			name: String,
			staticFriction: Float = 0.5f,
			dynamicFriction: Float = 0.5f,
			restitution: Float = 0.5f
		): PxMaterial {
			this.materials[name]?.destroy()
			return this.physics.createMaterial(staticFriction, dynamicFriction, restitution)
				.also { this.materials[name] = it }
		}

		init {
			val version = PxTopLevelFunctions.getPHYSICS_VERSION()
			this.foundation = PxTopLevelFunctions.CreateFoundation(
				version,
				this.allocator,
				this.errorHandler
			)
			val pvd: PxOmniPvd? = PxTopLevelFunctions.CreateOmniPvd(this.foundation)?.also {
				it.writer.setWriteStream(it.fileWriteStream)
				it.fileWriteStream.setFileName("PhysXTestTool.ovd")
				it.startSampling()
				this.logger.info("PhysX PVD sampling started.")
			}
			this.physics = PxTopLevelFunctions.CreatePhysics(
				version,
				this.foundation,
				this.tolerances,
				null,
				pvd
			)

			this.cpuDispatcher = PxTopLevelFunctions.DefaultCpuDispatcherCreate(
				Runtime.getRuntime().availableProcessors()
			)

			this.sceneDescription.gravity = PxVec3(0f, -9.807f, 0f)
			this.sceneDescription.cpuDispatcher = this.cpuDispatcher
			this.sceneDescription.filterShader = PxTopLevelFunctions.DefaultFilterShader()
			this.scene = this.physics.createScene(this.sceneDescription)
			var i = 0
			RenderBuffer.add(
				RenderLevelStageEvent.Stage.AFTER_SKY,
				{ event, _ ->
					if (!(this.noExecute || localClient.isPaused)) {
						i++
						if (i % 60 == 0) this.addCube(PxVec3(0.5f, 0.5f, 0.5f), "default")
						this.scene.simulate(event.partialTick.gameTimeDeltaTicks / 20)
						this.scene.fetchResults(true)
					}

					this.rigidActors.forEach { actor ->
						event.poseStack.pushPose()
						event.poseStack.initialTranslate(event.camera)
						event.poseStack.translate(actor.globalPose.p.x, actor.globalPose.p.y, actor.globalPose.p.z)
						event.poseStack.mulPose(
							Quaternionf(
								-actor.globalPose.q.x,
								-actor.globalPose.q.y,
								actor.globalPose.q.z,
								actor.globalPose.q.w
							)
						)
						localClient.blockRenderer.modelRenderer.renderBlockModel(
							event.poseStack.last(),
							localClient.renderBuffers().bufferSource(),
							ModBlocks.BREAD_BLOCK.get().block.defaultBlockState(),
							0x7FFFFFFF,
							OverlayTexture.NO_OVERLAY
						)
						event.poseStack.popPose()
					}

					false
				}
			)
			this.defineMaterial("default")
			this.resumeSimulation()
		}

		fun addCube(size: PxVec3, material: String) {
			val transform = PxTransform(PxIDENTITYEnum.PxIdentity)
			val boxGeometry = PxBoxGeometry(0.5f, 0.5f, 0.5f)
			val boxShape = this.physics.createShape(
				boxGeometry,
				this.materials[material] ?: throw IllegalArgumentException("Material $material not found"),
				true,
				PxShapeFlags(
					(PxShapeFlagEnum.eSCENE_QUERY_SHAPE.value or PxShapeFlagEnum.eSIMULATION_SHAPE.value).toByte()
				)
			)
			val box = this.physics.createRigidDynamic(transform)
			boxShape.simulationFilterData = PxFilterData(1, 1, 0, 0)
			box.attachShape(boxShape)
			this.scene.addActor(box)
			this.rigidActors.add(box)
			size.destroy()
		}

		fun cleanup() {
			this.scene.release()
			this.materials.forEach { (_, material) -> material.destroy() }
			this.materials.clear()
			this.rigidActors.forEach { it.release() }
			this.rigidActors.clear()
			this.sceneDescription.destroy()
			this.cpuDispatcher.destroy()
			this.physics.destroy()
			this.foundation.release()
			this.errorHandler.destroy()
			this.allocator.destroy()
		}
	}

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (level is ServerLevel) return InteractionResultHolder.pass(player.getItemInHand(usedHand))
		Thread.currentThread().contextClassLoader = this.classLoader

		try {
			this.physX::class.java.getDeclaredMethod("addCube")
				.invoke(this.physX, PxVec3(0.5f, 0.5f, 0.5f), "default")
		} catch (e: Throwable) {
			player.sendSystemMessage(Component.literal("PhysX failed to load: ${e.message}"))
			this.logger.error("PhysX failed to load", e)
		}

		return super.use(level, player, usedHand)
	}

	override val creativeModeTabs: List<Supplier<CreativeModeTab>> = listOf(ModCreativeTabs.EXPERIMENTAL_TAB)
}