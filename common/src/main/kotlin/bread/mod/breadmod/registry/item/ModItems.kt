package bread.mod.breadmod.registry.item

import bread.mod.breadmod.ModMainCommon
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object ModItems {
    val ITEM_REGISTRY: DeferredRegister<Item> = DeferredRegister.create(ModMainCommon.MOD_ID, Registries.ITEM)

    internal fun DeferredRegister<Block>.registerBlockItem(
        id: String,
        block: () -> Block,
        properties: Item.Properties
    ): RegistrySupplier<BlockItem> = this.register(id, block).let { blockSupply ->
        ITEM_REGISTRY.register(id) { BlockItem(blockSupply.get(), properties) }
    }
}