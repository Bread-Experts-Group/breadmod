package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.UP
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition.Builder
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.LOWER
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.MIDDLE
import org.bread_experts_group.breadmod.registry.block.actual.util.ModBlockStateProperties.TripleBlockHalf.UPPER

class DoubleOrNothingBlock : Block(Properties.of()), EntityBlock {
	companion object {
		val HALF: EnumProperty<TripleBlockHalf> = ModBlockStateProperties.TRIPLE_BLOCK_HALF
		val FACING: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
	}

	override fun createBlockStateDefinition(builder: Builder<Block, BlockState>) {
		builder.add(Companion.HALF, Companion.FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
		val pos = context.clickedPos
		val level = context.level
		return if (
			pos.y < level.maxBuildHeight - 2 &&
			level.getBlockState(pos.above()).canBeReplaced(context) &&
			level.getBlockState(pos.above().above()).canBeReplaced(context)
		) {
			return this.defaultBlockState()
				.setValue(Companion.FACING, context.horizontalDirection.opposite)
				.setValue(Companion.HALF, LOWER)
		} else null
	}

	override fun setPlacedBy(level: Level, pos: BlockPos, state: BlockState, placer: LivingEntity?, stack: ItemStack) {
		level.setBlockAndUpdate(pos.above(), state.setValue(Companion.HALF, MIDDLE))
		level.setBlockAndUpdate(pos.above().above(), state.setValue(Companion.HALF, UPPER))
	}

	override fun updateShape(
		state: BlockState,
		direction: Direction,
		neighborState: BlockState,
		level: LevelAccessor,
		pos: BlockPos,
		neighborPos: BlockPos
	): BlockState {
		val half = state.getValue(Companion.HALF)
		// todo only breaks all three blocks when the middle or lower block is broken, upper block doesn't break the other two.
		//  look into DoorBlock and try to make a three block tall variant that works properly..
		return if (direction.axis != Direction.Axis.Y || half == LOWER != (direction == UP)) {
			if (half == LOWER && direction == DOWN && !state.canSurvive(level, pos)) Blocks.AIR.defaultBlockState()
			else super.updateShape(state, direction, neighborState, level, pos, neighborPos)
		} else {
			if (neighborState.block is DoubleOrNothingBlock && neighborState.getValue(Companion.HALF) != half)
				neighborState.setValue(Companion.HALF, half) else Blocks.AIR.defaultBlockState()
		}
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? =
		if (state.getValue(Companion.HALF) == UPPER || state.getValue(Companion.HALF) == MIDDLE) null
		else DoubleOrNothingBlockEntity(pos, state)
}