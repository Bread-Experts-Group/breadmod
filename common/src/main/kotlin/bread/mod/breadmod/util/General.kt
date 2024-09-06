package bread.mod.breadmod.util

import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

fun DeferredRegister<Block>.registerBlockItem(
    itemRegister: DeferredRegister<Item>,
    id: String,
    block: () -> Block,
    properties: Item.Properties
): RegistrySupplier<Block> =
    this.register(id, block).also { block -> itemRegister.register(id) { BlockItem(block.get(), properties) } }