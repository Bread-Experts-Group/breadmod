package breadmodadvanced.registry

import breadmodadvanced.ModMainAdv.LOGGER
import breadmodadvanced.registry.block.ModBlockEntitiesAdv
import breadmodadvanced.registry.block.ModBlocksAdv
import breadmodadvanced.registry.item.ModItemsAdv
import breadmodadvanced.registry.recipe.ModRecipeSerializersAdv
import breadmodadvanced.registry.recipe.ModRecipeTypesAdv
import breadmodadvanced.registry.screen.ModCreativeTabsAdv
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

internal val registerList = setOf(
    ModBlocksAdv.deferredRegister,
    ModItemsAdv.deferredRegister,
    ModRecipeTypesAdv.deferredRegister,
    ModRecipeSerializersAdv.deferredRegister,
    ModBlockEntitiesAdv.deferredRegister,
    ModCreativeTabsAdv.deferredRegister
)

fun registerConfigs() = ModLoadingContext.get().let {
    LOGGER.info("Registering configs")
    it.registerConfig(ModConfig.Type.COMMON, ModConfigurationAdv.COMMON_SPECIFICATION.right, "breadmod-adv-common.toml")
}

internal fun registerAll(bus: IEventBus) {
    registerList.forEach {
        LOGGER.info("Pushing register for ${it.registryName}")
        it.register(bus)
    }
    registerConfigs()
}