package org.bread_experts_group.breadmod.registry.item.actual

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
                    name.startsWith("org.bread_experts_group.breadmod.physx.")
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

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level is ServerLevel) return InteractionResultHolder.pass(player.getItemInHand(usedHand))
        Thread.currentThread().contextClassLoader = classLoader

        try {
            val pxTL = Class.forName("physx.PxTopLevelFunctions", true, classLoader)
            val version: Int = pxTL.getMethod("getPHYSICS_VERSION").invoke(null) as Int

            val versionMajor = version shr 24
            val versionMinor = (version shr 16) and 0xff
            val versionMicro = (version shr 8) and 0xff
            player.sendSystemMessage(
                Component.literal("PhysX appears OK, seeing version $versionMajor.$versionMinor.$versionMicro")
            )

            Class.forName("org.bread_experts_group.breadmod.physx.PhysX", true, classLoader)
                .getMethod("runTest", Int::class.java, Player::class.java)
                .invoke(null, version, player)
        } catch (e: Throwable) {
            player.sendSystemMessage(Component.literal("PhysX failed to load: ${e.message}"))
            logger.error("PhysX failed to load", e)
        }

        return super.use(level, player, usedHand)
    }
}