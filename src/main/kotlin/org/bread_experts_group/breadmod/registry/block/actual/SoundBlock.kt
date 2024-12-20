package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import org.bread_experts_group.breadmod.registry.block.actual.entity.SoundBlockEntity

class SoundBlock : BaseEntityBlock(
	Properties.of()
		.strength(4f, 6f)
		.mapColor(MapColor.COLOR_GRAY)
		.requiresCorrectToolForDrops()
		.sound(SoundType.METAL)
) {
	companion object {
		val CODEC : MapCodec<out BaseEntityBlock> = simpleCodec { SoundBlock() }
	}

	init {
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
		)
	}

	override fun codec() : MapCodec<out BaseEntityBlock> = Companion.CODEC
	override fun newBlockEntity(pos : BlockPos, state : BlockState) : BlockEntity = SoundBlockEntity(pos, state)
	override fun getRenderShape(state : BlockState) : RenderShape = RenderShape.MODEL
	override fun getStateForPlacement(context : BlockPlaceContext) : BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)

	override fun createBlockStateDefinition(builder : StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}
}