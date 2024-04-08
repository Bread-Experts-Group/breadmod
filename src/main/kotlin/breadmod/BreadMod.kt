package breadmod

import breadmod.block.registry.ModBlockEntities
import breadmod.block.registry.ModBlocks
import breadmod.datagen.ModSounds
import breadmod.entity.ModEntities
import breadmod.entity.ModPainting
import breadmod.screens.creative.MainTab
import breadmod.item.ModItems
import breadmod.network.PacketHandler
import breadmod.recipe.ModRecipeSerializers
import breadmod.recipe.ModRecipeTypes
import breadmod.screens.ModMenuTypes
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.data.LanguageProvider
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

    fun modLocation(path: String): ResourceLocation
        = ResourceLocation(ID, path)
    fun modTranslatable(type: String = "misc", vararg path: String): MutableComponent
        = Component.translatable("$type.$ID.${path.joinToString(".")}")
    fun LanguageProvider.modAdd(value: String, type: String = "misc", vararg path: String)
        = add("$type.$ID.${path.joinToString(".")}", value)

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        LOGGER.info("Hello world!")

        LOGGER.info("Registering Mod Blocks")
        ModBlocks.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Items")
        ModItems.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Block Entity Types")
        ModBlockEntities.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Sounds")
        ModSounds.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Creative Tab")
        MainTab.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Recipe Types")
        ModRecipeSerializers.REGISTRY.register(MOD_BUS)
        ModRecipeTypes.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Menu Types")
        ModMenuTypes.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Entities")
        ModEntities.REGISTRY.register(MOD_BUS)
        LOGGER.info("Registering Mod Paintings")
        ModPainting.REGISTRY.register(MOD_BUS)

        PacketHandler

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