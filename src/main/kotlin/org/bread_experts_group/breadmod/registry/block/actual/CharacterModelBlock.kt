package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class CharacterModelBlock : Block(Properties.of().noOcclusion()) {
	override fun createBlockStateDefinition(pBuilder : StateDefinition.Builder<Block, BlockState>) {
		pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(pContext : BlockPlaceContext) : BlockState = this.defaultBlockState()
		.setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection)
}