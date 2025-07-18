package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class CoffeeMachineBlock : Block(BlockBehaviour.Properties.of()) {
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
}