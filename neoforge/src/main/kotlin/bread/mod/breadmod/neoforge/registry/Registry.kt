package bread.mod.breadmod.neoforge.registry

import bread.mod.breadmod.neoforge.registry.block.ModBlocksForge
import bread.mod.breadmod.neoforge.registry.item.ModItemsForge
import net.neoforged.bus.api.IEventBus

internal val registerList = listOf(
    ModBlocksForge.BLOCK_REGISTRY_FORGE,
    ModItemsForge.ITEM_REGISTRY_FORGE
)

internal fun registerAllForge(bus: IEventBus) {
    registerList.forEach {
        it.register(bus)
    }
}