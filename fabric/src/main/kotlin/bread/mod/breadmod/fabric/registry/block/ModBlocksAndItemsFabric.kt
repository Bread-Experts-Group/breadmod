package bread.mod.breadmod.fabric.registry.block

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.block.BreadBlock
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

internal object ModBlocksAndItemsFabric {
    fun <T: Block> registerBlock(block: T, name: String, shouldRegisterItem: Boolean): Block {
        if (shouldRegisterItem) {
            val item = BlockItem(block,  Item.Properties())
            Registry.register(BuiltInRegistries.ITEM, modLocation(name), item)
        }

        return Registry.register(BuiltInRegistries.BLOCK, modLocation(name), block)
    }

    // NOOOOOOOOOOOOOOOO
    fun registerBlocksAndItems() {
        registerBlock(BreadBlock(), "bread_block", true)
    }
}