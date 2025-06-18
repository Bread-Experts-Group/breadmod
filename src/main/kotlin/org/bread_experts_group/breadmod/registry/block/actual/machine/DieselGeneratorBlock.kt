package org.bread_experts_group.breadmod.registry.block.actual.machine

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlockWithEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DieselGeneratorBlockEntity

class DieselGeneratorBlock : BreadModBlockWithEntity(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
		DieselGeneratorBlockEntity(pos, state)

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
}