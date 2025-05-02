package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResult.FAIL
import net.minecraft.world.InteractionResult.sidedSuccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes.MONITOR
import org.bread_experts_group.breadmod.registry.block.actual.entity.KeyboardBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.MonitorBlockEntity
import org.bread_experts_group.breadmod.util.directionalTargetFaceSection
import org.bread_experts_group.breadmod.util.normalizedHitPos

class MonitorBlock : Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)), EntityBlock {
	init {
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
		)
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val direction = hitResult.direction ?: return FAIL
		val normalizedPos = normalizedHitPos(hitResult.location, pos)

		if (directionalTargetFaceSection(direction, normalizedPos, 0.12, 0.18, 0.82, 0.88, 0.00, 0.06)) {
			level.getBlockEntity(pos, MONITOR.get()).ifPresent {
				if (!it.isRunning()) it.start()
				else it.computer.reset()
				level.playSound(null, pos, SoundEvents.NOTE_BLOCK_BIT.value(), BLOCKS, 1f, 1f)
			}
			return sidedSuccess(level.isClientSide)
		}
		return super.useWithoutItem(state, level, pos, player, hitResult)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = MonitorBlockEntity(pos, state)
	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HorizontalDirectionalBlock.FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState().setValue(
			HorizontalDirectionalBlock.FACING,
			context.horizontalDirection.opposite
		)

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (!state.`is`(newState.block)) {
			val monitorEntity = level.getBlockEntity(pos) as MonitorBlockEntity
			val keyboardEntity = level.getBlockEntity(monitorEntity.keyboardPos) as? KeyboardBlockEntity
			keyboardEntity?.monitorPos = BlockPos.ZERO
			if (level.isClientSide) localClient.player?.sendSystemMessage(Component.literal("unbinding keyboard from removed monitor."))
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}