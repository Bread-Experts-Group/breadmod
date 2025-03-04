package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes.MONITOR
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadScreenBlockEntity

class MonitorBlock : Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)), EntityBlock {
	init {
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(DirectionalBlock.FACING, Direction.NORTH)
		)
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		level.getBlockEntity(pos, MONITOR.get()).ifPresent {
			it.start()
		}
		return super.useWithoutItem(state, level, pos, player, hitResult)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = BreadScreenBlockEntity(pos, state)
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(DirectionalBlock.FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(DirectionalBlock.FACING, context.nearestLookingDirection.opposite)
}