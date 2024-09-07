package bread.mod.breadmod.registry

import bread.mod.breadmod.networking.Networking.registerNetworking
import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.item.ModItems
import bread.mod.breadmod.registry.menu.ModCreativeTabs
import bread.mod.breadmod.registry.sound.ModSounds

internal object Registry {
    fun registerAll() {
        ModBlocks.BLOCK_REGISTRY.register()
        ModCreativeTabs.CREATIVE_TAB_REGISTRY.register()
        ModSounds.SOUND_REGISTRY.register()
        ModItems.ITEM_REGISTRY.register()
        registerNetworking()
        CommonEvents.registerServerTickEvent()
        CommonEvents.registerCommands()
//        registerClientTick()


    }
}