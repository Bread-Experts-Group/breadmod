package bread.mod.breadmod.registry

import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.item.ModItems

object Registry {
    val registerList = setOf(
        ModBlocks.deferredRegister,
        ModItems.deferredRegister
    )

    // TODO something about configs..
    fun registerConfigs() {}

    fun registerAll() {
        registerList.forEach {
            it.register()
        }
    }
}