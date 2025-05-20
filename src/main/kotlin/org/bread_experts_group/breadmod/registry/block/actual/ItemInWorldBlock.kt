package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.PushReaction.PUSH_ONLY
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemInWorldBlockEntity

class ItemInWorldBlock : BreadModBlockWithEntity(Properties.of().noOcclusion().noCollission().pushReaction(PUSH_ONLY)) {
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
		ItemInWorldBlockEntity(pos, state)

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(BlockStateProperties.FACING, context.nearestLookingDirection.opposite)

	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true
	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape {
		val direction = state.getValue(BlockStateProperties.FACING) ?: return Shapes.block()
		return when (direction) {
			DOWN  -> Block.box(0.0, 15.0, 0.0, 16.0, 16.0, 16.0)
			UP    -> Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0)
			NORTH -> Block.box(0.0, 0.0, 15.0, 16.0, 16.0, 16.0)
			SOUTH -> Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 1.0)
			WEST  -> Block.box(15.0, 0.0, 0.0, 16.0, 16.0, 16.0)
			EAST  -> Block.box(0.0, 0.0, 0.0, 1.0, 16.0, 16.0)
		}
	}

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.INVISIBLE

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (!state.`is`(newState.block)) {
			val entity = level.getBlockEntity(pos) as ItemInWorldBlockEntity
			entity.dropContents(level, pos)
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}