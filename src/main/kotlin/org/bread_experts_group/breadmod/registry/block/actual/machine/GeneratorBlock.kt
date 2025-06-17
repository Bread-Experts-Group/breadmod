package org.bread_experts_group.breadmod.registry.block.actual.machine

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
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.util.combine
import java.util.stream.Stream

class GeneratorBlock : Block(Properties.of()) {
	companion object {
		val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
		val POWERED: BooleanProperty = BlockStateProperties.POWERED
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
		val SHAPE_SOUTH: VoxelShape = Stream.of(
			box(2.0, 0.0, 1.0, 4.0, 1.0, 15.0),
			box(0.0, 0.0, 15.0, 16.0, 1.0, 16.0),
			box(0.0, 0.0, 0.0, 16.0, 1.0, 1.0),
			box(15.0, 1.0, 0.0, 16.0, 16.0, 1.0),
			box(0.0, 1.0, 0.0, 1.0, 16.0, 1.0),
			box(0.0, 1.0, 15.0, 1.0, 16.0, 16.0),
			box(15.0, 1.0, 15.0, 16.0, 16.0, 16.0),
			box(15.0, 15.0, 1.0, 16.0, 16.0, 15.0),
			box(0.0, 15.0, 1.0, 1.0, 16.0, 15.0),
			box(1.0, 15.0, 12.0, 15.0, 16.0, 14.0),
			box(1.0, 15.0, 2.0, 15.0, 16.0, 4.0),
			box(4.0, 15.0, 4.0, 12.0, 16.0, 12.0),
			box(0.0, 12.0, 7.0, 1.0, 15.0, 9.0),
			box(0.0, 7.0, 12.0, 1.0, 9.0, 15.0),
			box(0.0, 7.0, 1.0, 1.0, 9.0, 4.0),
			box(0.0, 4.0, 4.0, 1.0, 12.0, 12.0),
			box(1.0, 5.0, 11.0, 15.0, 12.0, 16.0),
			box(7.0, 1.0, 11.0, 16.0, 5.0, 15.0),
			box(5.0, 1.0, 11.0, 7.0, 5.0, 14.0),
			box(1.0, 1.0, 1.0, 15.0, 15.0, 11.0),
			box(12.0, 0.0, 1.0, 14.0, 1.0, 15.0)
		).combine()
		val SHAPE_EAST: VoxelShape = Stream.of(
			box(1.0, 0.0, 12.0, 15.0, 1.0, 14.0),
			box(15.0, 0.0, 0.0, 16.0, 1.0, 16.0),
			box(0.0, 0.0, 0.0, 1.0, 1.0, 16.0),
			box(0.0, 1.0, 0.0, 1.0, 16.0, 1.0),
			box(0.0, 1.0, 15.0, 1.0, 16.0, 16.0),
			box(15.0, 1.0, 15.0, 16.0, 16.0, 16.0),
			box(15.0, 1.0, 0.0, 16.0, 16.0, 1.0),
			box(1.0, 15.0, 0.0, 15.0, 16.0, 1.0),
			box(1.0, 15.0, 15.0, 15.0, 16.0, 16.0),
			box(12.0, 15.0, 1.0, 14.0, 16.0, 15.0),
			box(2.0, 15.0, 1.0, 4.0, 16.0, 15.0),
			box(4.0, 15.0, 4.0, 12.0, 16.0, 12.0),
			box(7.0, 12.0, 15.0, 9.0, 15.0, 16.0),
			box(12.0, 7.0, 15.0, 15.0, 9.0, 16.0),
			box(1.0, 7.0, 15.0, 4.0, 9.0, 16.0),
			box(4.0, 4.0, 15.0, 12.0, 12.0, 16.0),
			box(11.0, 5.0, 1.0, 16.0, 12.0, 15.0),
			box(11.0, 1.0, 0.0, 15.0, 5.0, 9.0),
			box(11.0, 1.0, 9.0, 14.0, 5.0, 11.0),
			box(1.0, 1.0, 1.0, 11.0, 15.0, 15.0),
			box(1.0, 0.0, 2.0, 15.0, 1.0, 4.0)
		).combine()
		val SHAPE_WEST: VoxelShape = Stream.of(
			box(1.0, 0.0, 2.0, 15.0, 1.0, 4.0),
			box(0.0, 0.0, 0.0, 1.0, 1.0, 16.0),
			box(15.0, 0.0, 0.0, 16.0, 1.0, 16.0),
			box(15.0, 1.0, 15.0, 16.0, 16.0, 16.0),
			box(15.0, 1.0, 0.0, 16.0, 16.0, 1.0),
			box(0.0, 1.0, 0.0, 1.0, 16.0, 1.0),
			box(0.0, 1.0, 15.0, 1.0, 16.0, 16.0),
			box(1.0, 15.0, 15.0, 15.0, 16.0, 16.0),
			box(1.0, 15.0, 0.0, 15.0, 16.0, 1.0),
			box(2.0, 15.0, 1.0, 4.0, 16.0, 15.0),
			box(12.0, 15.0, 1.0, 14.0, 16.0, 15.0),
			box(4.0, 15.0, 4.0, 12.0, 16.0, 12.0),
			box(7.0, 12.0, 0.0, 9.0, 15.0, 1.0),
			box(1.0, 7.0, 0.0, 4.0, 9.0, 1.0),
			box(12.0, 7.0, 0.0, 15.0, 9.0, 1.0),
			box(4.0, 4.0, 0.0, 12.0, 12.0, 1.0),
			box(0.0, 5.0, 1.0, 5.0, 12.0, 15.0),
			box(1.0, 1.0, 7.0, 5.0, 5.0, 16.0),
			box(2.0, 1.0, 5.0, 5.0, 5.0, 7.0),
			box(5.0, 1.0, 1.0, 15.0, 15.0, 15.0),
			box(1.0, 0.0, 12.0, 15.0, 1.0, 14.0)
		).combine()
	}

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(Companion.FACING, Companion.POWERED)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(Companion.FACING, context.horizontalDirection.opposite)
			.setValue(Companion.POWERED, false)

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape =
		when (state.getValue(Companion.FACING)) {
			NORTH -> Companion.SHAPE_NORTH
			SOUTH -> Companion.SHAPE_SOUTH
			EAST  -> Companion.SHAPE_EAST
			WEST  -> Companion.SHAPE_WEST
			else  -> Shapes.block()
		}
}