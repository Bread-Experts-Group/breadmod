package breadmod.registry

import breadmod.BreadMod.LOGGER
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.dimension.ModDimensions
import breadmod.registry.entity.ModEntities
import breadmod.registry.entity.ModPainting
import breadmod.registry.item.ModItems
import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.registry.screen.ModMenuTypes
import breadmod.registry.sound.ModSounds
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.config.ModConfig

val registerList = setOf(
    ModBlocks.deferredRegister,
    ModItems.deferredRegister,
    ModBlockEntities.deferredRegister,
    ModDimensions.deferredRegister,
    ModEntities.deferredRegister,
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
    registerList.forEach {
        LOGGER.info("Pushing register for ${it.registryName}")
        it.register(bus)
    }
    registerConfigs()
}

