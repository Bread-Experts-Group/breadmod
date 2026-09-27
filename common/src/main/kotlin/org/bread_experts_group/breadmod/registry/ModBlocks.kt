package org.bread_experts_group.breadmod.registry

import net.minecraft.world.item.BlockItem
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockBehaviour
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryBlockItem
import org.bread_experts_group.breadmod.Breadmod

object ModBlocks {
    val REGISTRY: RegistryProvider.Blocks = RegistryProvider.getBlocks(Breadmod.MOD_ID)

    val BREAD_BLOCK: RegistryBlockItem<Block, BlockItem> = REGISTRY.registerSimpleBlock<Block>(
        "bread_block",
        BlockBehaviour.Properties.ofFullCopy(Blocks.HAY_BLOCK)
    ).withItem()
}