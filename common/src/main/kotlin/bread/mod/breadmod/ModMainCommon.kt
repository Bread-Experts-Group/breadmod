package bread.mod.breadmod

import bread.mod.breadmod.logging.ConsoleColorAppender
import bread.mod.breadmod.registry.ClientEventRegistry
import bread.mod.breadmod.registry.Registry.registerAll
import bread.mod.breadmod.registry.Registry.runAnnotations
import bread.mod.breadmod.registry.config.ClientConfig
import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator

object ModMainCommon {
    const val MOD_ID: String = "breadmod"

    fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation =
        path.toMutableList().let {
            ResourceLocation.fromNamespaceAndPath(if (override) it.removeFirst() else MOD_ID, it.joinToString("/"))
        }

    fun modTranslatable(type: String = "misc", vararg path: String, args: List<Any> = listOf()): MutableComponent =
        Component.translatable("$type.$MOD_ID.${path.joinToString(".")}", *args.toTypedArray())

    /**
     * Initializes all common specific classes.
     */
    fun init() {
        if (Platform.isDevelopmentEnvironment() || Platform.getEnvironment() == Env.SERVER) {
            val ctx = LogManager.getContext(false) as LoggerContext
            val uri = this::class.java.getResource("/log4j2.xml")?.toURI()
                ?: throw IllegalStateException("Failed to load log4j2.xml")
            val cfg = ConfigurationFactory.getInstance().getConfiguration(ctx, ctx.name, uri, null)

            val clrApd = ConsoleColorAppender.createAppender("ConsoleColorAppender", null)
            cfg.addAppender(clrApd)
            Configurator.reconfigure(cfg)
        }

        // Register all our mod contents
        registerAll()
    }

    /**
     * Initializes all client specific classes.
     */
    fun initClient() {
        ClientConfig.initialize()
        ClientEventRegistry.registerOverlays()
        ClientEventRegistry.registerClientCommands()
        ClientEventRegistry.registerEntityRenderers()
//        ClientEvents.registerKeyEvent()
        ClientEventRegistry.registerMouseEvent()
        ClientEventRegistry.registerBlockEntityRenderers()
        ClientEventRegistry.registerEntityLayers()
        ClientEventRegistry.renderBEWLRs()
        ClientEventRegistry.registerMachTrailTicker()

        runAnnotations()
    }
}
