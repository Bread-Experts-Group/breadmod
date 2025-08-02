package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.client.render.entity.block.MonitorRenderer
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ComputerHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.KeyboardStateHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.KeyboardStateHandler.Companion.MONITOR_POSITION
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.MonitorStateHandler
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state.MonitorStateHandler.Companion.KEYBOARD_POSITION
import org.bread_experts_group.breadmod.util.directionalTargetFaceSection
import org.bread_experts_group.breadmod.util.normalizedHitPos

class MonitorBlock : BreadModBlock(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	init {
		this.registerDefaultState(
			this.stateDefinition.any()
				.setValue(HORIZONTAL_FACING, Direction.NORTH)
		)
	}

	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val state = MonitorStateHandler()
		val computer = ComputerHandler()
		return mapOf(
			MonitorStateHandler.BLOCK_VOID to mapOf(null to { _ -> state }),
			ComputerHandler.BLOCK_VOID to mapOf(null to { _ -> computer })
		)
	}

	override fun ofRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<out BreadModBlockEntity>)? =
		::MonitorRenderer

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (!level.isClientSide) return InteractionResult.PASS
		val direction = hitResult.direction ?: return InteractionResult.FAIL
		val normalizedPos = normalizedHitPos(hitResult.location, pos)

		if (
			directionalTargetFaceSection(
				direction, normalizedPos,
				0.12, 0.18, 0.82, 0.88,
				0.00, 0.06
			)
		) {
			val monitorEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return InteractionResult.FAIL
			val monitorComputer = monitorEntity.getCapability(ComputerHandler.BLOCK_VOID)
			if (monitorComputer.running) monitorComputer.reset() else monitorComputer.start()
			level.playSound(
				null, pos, SoundEvents.NOTE_BLOCK_BIT.value(), SoundSource.BLOCKS,
				1f, 1f
			)
		}
		return InteractionResult.SUCCESS
	}

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
			val monitorEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return
			val monitorState = monitorEntity.getCapability(MonitorStateHandler.BLOCK_VOID)
			val keyboardEntity = level.getBlockEntity(monitorState.getOrNull(KEYBOARD_POSITION) ?: return)
					as? BreadModBlockEntity ?: return
			val keyboardState = keyboardEntity.getCapability(KeyboardStateHandler.BLOCK_VOID)
			keyboardState.set(MONITOR_POSITION, null)
			if (level.isClientSide) localClient.player?.sendSystemMessage(Component.literal("unbinding keyboard from removed monitor."))
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}