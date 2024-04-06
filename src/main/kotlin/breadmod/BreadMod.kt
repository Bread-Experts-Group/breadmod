package breadmod

import breadmod.block.registry.ModBlockEntities
import breadmod.block.registry.ModBlocks
import breadmod.datagen.ModSounds
import breadmod.entity.ModEntities
import breadmod.gui.ModCreativeTab
import breadmod.item.ModItems
import breadmod.recipe.ModRecipeSerializers
import breadmod.recipe.ModRecipeTypes
import breadmod.screens.ModMenuTypes
import net.minecraft.client.Minecraft
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Suppress("Unused")
@Mod(BreadMod.ID)
object BreadMod {
    const val ID = "breadmod"

    // the logger for our mod
    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.info("Hello world!")

        // Register the KDeferredRegister to the mod-specific event bus
        LOGGER.info("Registering Mod Blocks")
        ModBlocks.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Items")
        ModItems.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Block Entity Types")
        ModBlockEntities.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Sounds")
        ModSounds.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Creative Tab")
        ModCreativeTab.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Recipe Types")
        ModRecipeSerializers.REGISTRY.register(MOD_BUS)
        ModRecipeTypes.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Menu Types")
        ModMenuTypes.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Entities")
        ModEntities.REGISTRY.register(MOD_BUS)

        LOGGER.info("Registering Mod Config")
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPECIFICATION.right, "breadmod-common.toml")

        runForDist(
            clientTarget = {
                MOD_BUS.addListener(BreadMod::onClientSetup)
                Minecraft.getInstance()
            },
            serverTarget = {
                MOD_BUS.addListener(BreadMod::onServerSetup)
                "test"
            }
        )
    }

    /**
     * This is used for initializing client specific
     * things such as renderers and keymaps
     * Fired on the mod specific event bus.
     */
    private fun onClientSetup(event: FMLClientSetupEvent) {
        LOGGER.info("Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.info("Server starting...")
    }
}