package bread.mod.breadmod.neoforge.registry.item

import bread.mod.breadmod.ModMainCommon
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.registries.DeferredRegister
import java.util.function.Supplier

internal object ModItemsForge {
    val ITEM_REGISTRY_FORGE: DeferredRegister.Items = DeferredRegister.createItems(ModMainCommon.MOD_ID)

    internal fun DeferredRegister.Blocks.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Item.Properties
    ): Supplier<BlockItem> = this.register(id, block).let { blockSupply ->
        ITEM_REGISTRY_FORGE.registerItem(id) { BlockItem(blockSupply.get(), properties) }
    }
}