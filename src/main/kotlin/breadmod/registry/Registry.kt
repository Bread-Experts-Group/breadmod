package breadmod.registry

import breadmod.ModMain.LOGGER
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.registry.block.ModBlocks
import breadmod.registry.entity.ModEntityTypes
import breadmod.registry.entity.ModPainting
import breadmod.registry.fluid.ModFluids
import breadmod.registry.item.ModItems
import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.registry.menu.ModCreativeTabs
import breadmod.registry.menu.ModMenuTypes
import breadmod.registry.sound.ModSounds
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

val registerList = setOf(
    ModFluids.deferredRegister,
    ModFluids.deferredTypesRegister,
    ModBlocks.deferredRegister,
    ModItems.deferredRegister,
    ModCreativeTabs.deferredRegister,
    ModBlockEntityTypes.deferredRegister,
    ModEntityTypes.deferredRegister,
    ModPainting.deferredRegister,
    ModRecipeTypes.deferredRegister,
    ModRecipeSerializers.deferredRegister,
    ModMenuTypes.deferredRegister,
    ModSounds.deferredRegister
)

fun registerConfigs() = ModLoadingContext.get().let {
    LOGGER.info("Registering configs")
    it.registerConfig(ModConfig.Type.COMMON, ModConfiguration.COMMON_SPECIFICATION.right, "breadmod-common.toml")
}

fun registerAll(bus: IEventBus) {
    registerConfigs()
    registerList.forEach {
        LOGGER.info("Pushing register for ${it.registryName}")
        it.register(bus)
    }
}

