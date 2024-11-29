package org.bread_experts_group.breadmod

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.common.data.LanguageProvider
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.apache.logging.log4j.core.LoggerContext
import org.apache.logging.log4j.core.config.ConfigurationFactory
import org.apache.logging.log4j.core.config.Configurator
import org.bread_experts_group.breadmod.logging.ConsoleColorAppender
import org.bread_experts_group.breadmod.registry.ModConfiguration
import org.bread_experts_group.breadmod.registry.Registry
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import java.nio.file.Path

/**
 * Main mod class.
 */
@Mod(BreadMod.ID)
class BreadMod(container: ModContainer) {
    companion object {
        /**
         * ID for breadmod.
         */
        const val ID: String = "breadmod"

        // the logger for our mod
        val LOGGER: Logger = LogManager.getLogger(ID)

        val DATA_DIR: Path? = FMLPaths.CONFIGDIR.get().resolve(ID)

        /**
         * @param override Only use this when you need to refer to a namespace outside breadmod
         */
        fun modLocation(vararg path: String, override: Boolean = false): ResourceLocation =
            path.toMutableList().let {
                ResourceLocation.fromNamespaceAndPath(
                    if (override) it.removeFirst() else ID, it.joinToString("/")
                )
            }

        fun modTranslatable(type: String = "misc", vararg path: String, args: List<Any> = listOf()): MutableComponent =
            Component.translatable("$type.$ID.${path.joinToString(".")}", *args.toTypedArray())

        fun LanguageProvider.modAdd(value: String, type: String = "misc", vararg path: String): Unit =
            add("$type.$ID.${path.joinToString(".")}", value)
    }

    init {
        if (!FMLLoader.isProduction() || FMLLoader.getDist() == Dist.DEDICATED_SERVER) {
            val ctx = LogManager.getContext(false) as LoggerContext
            val uri = this::class.java.getResource("/log4j2.xml")?.toURI()
                ?: throw IllegalStateException("Failed to load log4j2.xml")
            val cfg = ConfigurationFactory.getInstance().getConfiguration(ctx, ctx.name, uri, null)

            val clrApd = ConsoleColorAppender.createAppender("ConsoleColorAppender", null)
            cfg.addAppender(clrApd)
            Configurator.reconfigure(cfg)
        }

        LOGGER.log(Level.INFO, "Hello world!")

        container.registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPEC.right, "breadmod-common.toml")
        container.registerConfig(ModConfig.Type.CLIENT, ModConfiguration.CLIENT_SPEC.right, "breadmod-client.toml")

        // Register the KDeferredRegister to the mod-specific event bus
        Registry.registerAll(MOD_BUS)

//        val obj = runForDist(clientTarget = {
//            MOD_BUS.addListener(::onClientSetup)
//            Minecraft.getInstance()
//        }, serverTarget = {
//            MOD_BUS.addListener(::onServerSetup)
//            "test"
//        })

//        println(obj)
    }

//    /**
//     * This is used for initializing client specific
//     * things such as renderers and keymaps
//     * Fired on the mod specific event bus.
//     */
//    private fun onClientSetup(event: FMLClientSetupEvent) {
//        LOGGER.log(Level.INFO, "Initializing client...")
//    }
//
//    /**
//     * Fired on the global Forge bus.
//     */
//    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
//        LOGGER.log(Level.INFO, "Server starting...")
//    }
}
