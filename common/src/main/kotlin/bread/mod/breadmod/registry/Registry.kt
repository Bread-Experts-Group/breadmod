package bread.mod.breadmod.registry

import bread.mod.breadmod.networking.Networking.registerNetworking
import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.item.ModItems

internal object Registry {
    fun registerAll() {
        ModBlocks.BLOCK_REGISTRY.register()
        ModItems.ITEM_REGISTRY.register()
        registerNetworking()
    }
}