package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.screen.KeyboardScreen
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.KeyboardBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.MonitorBlockEntity
import org.bread_experts_group.breadmod.util.threeByThreeAABB

class KeyboardBlock : BreadModBlockWithEntity(
	Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()
), SimpleWaterloggedBlock {
	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (player.isShiftKeyDown) {
			val keyboardEntity = level.getBlockEntity(pos) as KeyboardBlockEntity
			val posList: List<BlockPos> = buildList {
				BlockPos.betweenClosedStream(threeByThreeAABB(pos)).forEach { this.add(it.immutable()) }
			}.filter {
				val subState = level.getBlockState(it)
				val entity = level.getBlockEntity(it) as? MonitorBlockEntity ?: return@filter false
				subState.`is`(ModBlocks.MONITOR.asBlock()) && !entity.isKeyboardBound()
			}
			if (posList.isEmpty()) {
				if (level.isClientSide) player.sendSystemMessage(Component.literal("no unbound monitors found, cancelling bind."))
				return super.useWithoutItem(state, level, pos, player, hitResult)
			}

			if (!keyboardEntity.isMonitorBound()) for (monitorPos: BlockPos in posList) {
				val monitorEntity =
					level.getBlockEntity(monitorPos) as? MonitorBlockEntity ?: return super.useWithoutItem(
						state,
						level,
						pos,
						player,
						hitResult
					)
				if (!monitorEntity.isKeyboardBound()) {
					monitorEntity.keyboardPos = pos
					keyboardEntity.monitorPos = monitorEntity.blockPos
					if (level.isClientSide) {
						player.sendSystemMessage(Component.literal("bound to monitor at ${monitorEntity.blockPos} / bound monitor to keyboard at ${keyboardEntity.blockPos}"))
					}
					break
				}
				break
			}
		} else {
			val keyboardEntity = level.getBlockEntity(pos) as KeyboardBlockEntity
			if (level.isClientSide) {
				if (!keyboardEntity.isMonitorBound()) {
					player.sendSystemMessage(Component.literal("keyboard is not bound yet."))
				} else localClient.setScreen(KeyboardScreen(keyboardEntity.monitorPos))
			}
		}


		return super.useWithoutItem(state, level, pos, player, hitResult)
	}

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (!state.`is`(newState.block)) {
			val keyboardEntity = level.getBlockEntity(pos) as KeyboardBlockEntity
			(level.getBlockEntity(keyboardEntity.monitorPos) as? MonitorBlockEntity)?.keyboardPos = BlockPos.ZERO
			if (level.isClientSide) localClient.player?.sendSystemMessage(Component.literal("unbinding monitor from removed keyboard."))
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape = box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.WATERLOGGED)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = KeyboardBlockEntity(pos, state)

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(BlockStateProperties.WATERLOGGED, false)
}