package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.material.PushReaction.BLOCK
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.CableBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.toVec3

// todo cable network
class CableBlock : BreadModBlockWithEntity(Properties.of().noOcclusion().pushReaction(BLOCK)) {
	companion object {
		val UP: BooleanProperty = ModBlockStateProperties.UP
		val DOWN: BooleanProperty = ModBlockStateProperties.DOWN
		val NORTH: BooleanProperty = ModBlockStateProperties.NORTH
		val SOUTH: BooleanProperty = ModBlockStateProperties.SOUTH
		val EAST: BooleanProperty = ModBlockStateProperties.EAST
		val WEST: BooleanProperty = ModBlockStateProperties.WEST
		val CORE_SHAPE: VoxelShape = Block.box(5.0, 5.0, 5.0, 11.0, 11.0, 11.0)
		val UP_SHAPE: VoxelShape = Block.box(5.0, 11.0, 5.0, 11.0, 16.0, 11.0)
		val DOWN_SHAPE: VoxelShape = Block.box(5.0, 0.0, 5.0, 11.0, 5.0, 11.0)
		val NORTH_SHAPE: VoxelShape = Block.box(5.0, 5.0, 0.0, 11.0, 11.0, 5.0)
		val SOUTH_SHAPE: VoxelShape = Block.box(5.0, 5.0, 11.0, 11.0, 11.0, 16.0)
		val WEST_SHAPE: VoxelShape = Block.box(0.0, 5.0, 5.0, 5.0, 11.0, 11.0)
		val EAST_SHAPE: VoxelShape = Block.box(11.0, 5.0, 5.0, 16.0, 11.0, 11.0)
	}

	override fun neighborChanged(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		neighborBlock: Block,
		neighborPos: BlockPos,
		movedByPiston: Boolean
	) {
		val neighborState = level.getBlockState(neighborPos)
		val (nX, nY, nZ) = neighborPos.toVec3()
		val (x, y, z) = pos.toVec3()
		val side = Direction.getNearest(nX - x, nY - y, nZ - z)
		val isNeighborCable = level.getBlockState(neighborPos).`is`(ModBlocks.CABLE.asBlock())
		level.setBlockAndUpdate(pos, state.setValue(this.sideToProperty(side), isNeighborCable))
		if (isNeighborCable) level.setBlockAndUpdate(
			neighborPos,
			neighborState.setValue(this.sideToProperty(side.opposite), true)
		)
	}

	private fun sideToProperty(side: Direction): BooleanProperty = when (side) {
		Direction.DOWN  -> Companion.DOWN
		Direction.UP    -> Companion.UP
		Direction.NORTH -> Companion.NORTH
		Direction.SOUTH -> Companion.SOUTH
		Direction.WEST  -> Companion.WEST
		Direction.EAST  -> Companion.EAST
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(Companion.UP, false)
			.setValue(Companion.DOWN, false)
			.setValue(Companion.NORTH, false)
			.setValue(Companion.SOUTH, false)
			.setValue(Companion.EAST, false)
			.setValue(Companion.WEST, false)

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(Companion.UP, Companion.DOWN, Companion.NORTH, Companion.SOUTH, Companion.EAST, Companion.WEST)
	}

	override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
		var shape = Companion.CORE_SHAPE
		if (state.getValue(Companion.UP)) shape = Shapes.or(shape, Companion.UP_SHAPE)
		if (state.getValue(Companion.DOWN)) shape = Shapes.or(shape, Companion.DOWN_SHAPE)
		if (state.getValue(Companion.NORTH)) shape = Shapes.or(shape, Companion.NORTH_SHAPE)
		if (state.getValue(Companion.SOUTH)) shape = Shapes.or(shape, Companion.SOUTH_SHAPE)
		if (state.getValue(Companion.EAST)) shape = Shapes.or(shape, Companion.EAST_SHAPE)
		if (state.getValue(Companion.WEST)) shape = Shapes.or(shape, Companion.WEST_SHAPE)
		return shape
	}

	override fun hasDynamicShape(): Boolean = true

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = CableBlockEntity(pos, state)
}