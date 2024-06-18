package breadmodadvanced.registry

import breadmod.ModMain.LOGGER
import breadmodadvanced.registry.block.ModBlockEntitiesAdv
import breadmodadvanced.registry.block.ModBlocksAdv
import breadmodadvanced.registry.item.ModItemsAdv
import breadmodadvanced.registry.recipe.ModRecipeSerializersAdv
import breadmodadvanced.registry.recipe.ModRecipeTypesAdv
import net.minecraftforge.eventbus.api.IEventBus

val registerList = setOf(
    ModBlocksAdv.deferredRegister,
    ModItemsAdv.deferredRegister,
    ModRecipeTypesAdv.deferredRegister,
    ModRecipeSerializersAdv.deferredRegister,
    ModBlockEntitiesAdv.deferredRegister
)

fun registerAll(bus: IEventBus) {
    registerList.forEach {
        LOGGER.info("Pushing register for ${it.registryName}")
        it.register(bus)
    }
}