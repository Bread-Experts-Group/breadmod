package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.WEST
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.POWERED
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.util.combine
import org.bread_experts_group.breadmod.util.east
import org.bread_experts_group.breadmod.util.south
import org.bread_experts_group.breadmod.util.west
import java.util.stream.Stream

class GeneratorBlock : Block(Properties.of()) {
	companion object {
		val SHAPE_NORTH: VoxelShape = Stream.of(
			box(12.0, 0.0, 1.0, 14.0, 1.0, 15.0),
			box(0.0, 0.0, 0.0, 16.0, 1.0, 1.0),
			box(0.0, 0.0, 15.0, 16.0, 1.0, 16.0),
			box(0.0, 1.0, 15.0, 1.0, 16.0, 16.0),
			box(15.0, 1.0, 15.0, 16.0, 16.0, 16.0),
			box(15.0, 1.0, 0.0, 16.0, 16.0, 1.0),
			box(0.0, 1.0, 0.0, 1.0, 16.0, 1.0),
			box(0.0, 15.0, 1.0, 1.0, 16.0, 15.0),
			box(15.0, 15.0, 1.0, 16.0, 16.0, 15.0),
			box(1.0, 15.0, 2.0, 15.0, 16.0, 4.0),
			box(1.0, 15.0, 12.0, 15.0, 16.0, 14.0),
			box(4.0, 15.0, 4.0, 12.0, 16.0, 12.0),
			box(15.0, 12.0, 7.0, 16.0, 15.0, 9.0),
			box(15.0, 7.0, 1.0, 16.0, 9.0, 4.0),
			box(15.0, 7.0, 12.0, 16.0, 9.0, 15.0),
			box(15.0, 4.0, 4.0, 16.0, 12.0, 12.0),
			box(1.0, 5.0, 0.0, 15.0, 12.0, 5.0),
			box(0.0, 1.0, 1.0, 9.0, 5.0, 5.0),
			box(9.0, 1.0, 2.0, 11.0, 5.0, 5.0),
			box(1.0, 1.0, 5.0, 15.0, 15.0, 15.0),
			box(2.0, 0.0, 1.0, 4.0, 1.0, 15.0)
		).combine()
		val SHAPE_SOUTH: VoxelShape = this.SHAPE_NORTH.south()
		val SHAPE_EAST: VoxelShape = this.SHAPE_NORTH.east()
		val SHAPE_WEST: VoxelShape = this.SHAPE_NORTH.west()
	}

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, POWERED)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(POWERED, false)

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		when (state.getValue(HORIZONTAL_FACING)) {
			NORTH -> Companion.SHAPE_NORTH
			SOUTH -> Companion.SHAPE_SOUTH
			EAST -> Companion.SHAPE_EAST
			WEST -> Companion.SHAPE_WEST
			else -> Shapes.block()
		}
}