package breadmod

import breadmod.block.ModBlocks
import breadmod.datagen.ModSounds
import breadmod.gui.ModCreativeTab
import breadmod.item.ModItems
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

/**
 * Main mod class. Should be an `object` declaration annotated with `@Mod`.
 * The modid should be declared in this object and should match the modId entry
 * in mods.toml.
 *
 * An example for blocks is in the `blocks` package of this mod.
 */
@Suppress("SpellCheckingInspection", "Unused")
@Mod(BreadMod.ID)
object BreadMod {
    const val ID = "breadmod"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.log(Level.INFO, "Hello world!")

        // Register the KDeferredRegister to the mod-specific event bus
        LOGGER.log(Level.INFO, "Registering Mod Blocks")
        ModBlocks.REGISTRY.register(MOD_BUS)
        LOGGER.log(Level.INFO, "Registering Mod Items")
        ModItems.REGISTRY.register(MOD_BUS)
        LOGGER.log(Level.INFO, "Registering Mod Sounds")
        ModSounds.MOD_SOUNDS.register(MOD_BUS)
        LOGGER.log(Level.INFO, "Registering Creative Tab")
        ModCreativeTab.CREATIVE_MODE_TABS.register(MOD_BUS)

        LOGGER.log(Level.INFO, "Registering Mod Config")
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfiguration.SPECIFICATION, "breadmod-common.toml")

        val obj = runForDist(
            clientTarget = {
                MOD_BUS.addListener(BreadMod::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(BreadMod::onServerSetup)
                "test"
            })

        println(obj)
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }
}