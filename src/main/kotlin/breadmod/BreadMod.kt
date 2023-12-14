package breadmod

import breadmod.block.ModBlocks
import breadmod.recipes.ModRecipes
import breadmod.datagen.provider.ModSounds
import breadmod.item.ModItems
import net.minecraft.client.Minecraft
import net.minecraft.resources.ResourceLocation
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

@Mod(BreadMod.ID)
object BreadMod {
    const val ID = "breadmod"

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        ModBusEventHandler
        LOGGER.log(Level.INFO, "Hello world!")

        ModSounds.REGISTRY.register(MOD_BUS)
        ModBlocks.REGISTRY.register(MOD_BUS)
        ModItems.REGISTRY.register(MOD_BUS)
        ModRecipes.REGISTRY.register(MOD_BUS)

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ModConfiguration.SPECIFICATION, "breadmod-common.toml")

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
        LOGGER.log(Level.INFO, "Initializing client...")
    }

    /**
     * Fired on the global Forge bus.
     */
    private fun onServerSetup(event: FMLDedicatedServerSetupEvent) {
        LOGGER.log(Level.INFO, "Server starting...")
    }

    fun resource(name: String) = ResourceLocation(ID, name)
}