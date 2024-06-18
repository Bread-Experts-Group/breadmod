package breadmod.advanced.registry

import breadmod.ModMain.LOGGER
import breadmod.advanced.registry.block.ModBlocksAdv
import breadmod.advanced.registry.item.ModItemsAdv
import breadmod.registry.registerConfigs
import net.minecraftforge.eventbus.api.IEventBus

val registerList = setOf(
    ModBlocksAdv.deferredRegister,
    ModItemsAdv.deferredRegister
)

fun registerAll(bus: IEventBus) {
//    registerConfigs()
    registerList.forEach {
        LOGGER.info("Pushing register for ${it.registryName}")
        it.register(bus)
    }
}