package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadmod.client.gui.screens.KeyboardScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.CapabilityMap
import org.bread_experts_group.breadmod.registry.block.handler.state.KeyboardStateHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.KeyboardStateHandler.Companion.MONITOR_POSITION
import org.bread_experts_group.breadmod.registry.block.handler.state.MonitorStateHandler
import org.bread_experts_group.breadmod.registry.block.handler.state.MonitorStateHandler.Companion.KEYBOARD_POSITION
import org.bread_experts_group.breadmod.util.BlockScanner
import org.bread_experts_group.breadmod.util.BlockScanner.filterPositions
import org.bread_experts_group.breadmod.util.block
import org.bread_experts_group.breadmod.util.combine
import java.util.stream.Stream

class KeyboardBlock : BreadModBlock(
	Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()
), SimpleWaterloggedBlock {
	companion object {
		val SHAPE_NORTH: VoxelShape = Stream.of(
			box(0.0, 0.0, 9.0, 2.0, 0.5, 11.0),
			box(3.0, 0.0, 6.0, 16.0, 0.5, 11.0),
			box(0.0, 0.0, 5.0, 2.0, 0.5, 8.0),
			box(0.5, 0.5, 9.5, 1.5, 3.0, 10.5)
		).combine()
		val SHAPE_SOUTH: VoxelShape = Stream.of(
			box(14.0, 0.0, 5.0, 16.0, 0.5, 7.0),
			box(0.0, 0.0, 5.0, 13.0, 0.5, 10.0),
			box(14.0, 0.0, 8.0, 16.0, 0.5, 11.0),
			box(14.5, 0.5, 5.5, 15.5, 3.0, 6.5)
		).combine()
		val SHAPE_EAST: VoxelShape = Stream.of(
			box(5.0, 0.0, 0.0, 7.0, 0.5, 2.0),
			box(5.0, 0.0, 3.0, 10.0, 0.5, 16.0),
			box(8.0, 0.0, 0.0, 11.0, 0.5, 2.0),
			box(5.5, 0.5, 0.5, 6.5, 3.0, 1.5)
		).combine()
		val SHAPE_WEST: VoxelShape = Stream.of(
			box(9.0, 0.0, 14.0, 11.0, 0.5, 16.0),
			box(6.0, 0.0, 0.0, 11.0, 0.5, 13.0),
			box(5.0, 0.0, 14.0, 8.0, 0.5, 16.0),
			box(9.5, 0.5, 14.5, 10.5, 3.0, 15.5)
		).combine()
	}

	override fun shouldCreateEntity(with: Pair<BlockPos, BlockState>?): Boolean = true
	override fun ofCapabilities(): CapabilityMap<(BreadModBlockEntity) -> Any> {
		val state = KeyboardStateHandler()
		return mapOf(
			KeyboardStateHandler.BLOCK_VOID to mapOf(null to { _ -> state })
		)
	}

	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		val keyboardEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return InteractionResult.FAIL
		val keyboardState = keyboardEntity.getCapability(KeyboardStateHandler.BLOCK_VOID)
		if (player.isShiftKeyDown) {
			val opposite = state.getValue(HORIZONTAL_FACING).opposite
			val posList = BlockScanner.scanAdjacent(pos, opposite)
				.filterPositions(level, ModBlocks.MONITOR.block)
			if (posList.isEmpty()) {
				if (level.isClientSide) player.sendSystemMessage(Component.literal("no unbound monitors found, cancelling bind."))
				return super.useWithoutItem(state, level, pos, player, hitResult)
			}
			if (keyboardState.getOrNull(MONITOR_POSITION) == null) for (monitorPos: BlockPos in posList) {
				val monitorEntity = level.getBlockEntity(monitorPos) as? BreadModBlockEntity
					?: return InteractionResult.FAIL
				val monitorState = monitorEntity.getCapability(MonitorStateHandler.BLOCK_VOID)
				if (monitorState.getOrNull(KEYBOARD_POSITION) == null) {
					monitorState.set(KEYBOARD_POSITION, pos)
					keyboardState.set(MONITOR_POSITION, monitorPos)
					if (level.isClientSide) player.sendSystemMessage(Component.literal("bound to monitor at ${monitorEntity.blockPos} / bound monitor to keyboard at ${keyboardEntity.blockPos}"))
					break
				}
				break
			}
		} else {
			if (level.isClientSide) {
				val position = keyboardState.getOrNull(MONITOR_POSITION)
				if (position == null) {
					player.sendSystemMessage(Component.literal("keyboard is not bound yet."))
				} else localClient.setScreen(KeyboardScreen(position))
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
			val keyboardEntity = level.getBlockEntity(pos) as? BreadModBlockEntity ?: return
			val keyboardState = keyboardEntity.getCapability(KeyboardStateHandler.BLOCK_VOID)
			val monitorPosition = keyboardState.getOrNull(MONITOR_POSITION) ?: return
			val monitorEntity = level.getBlockEntity(monitorPosition) as? BreadModBlockEntity ?: return
			val monitorState = monitorEntity.getCapability(MonitorStateHandler.BLOCK_VOID)
			monitorState.set(KEYBOARD_POSITION, null)
			if (level.isClientSide) localClient.player?.sendSystemMessage(Component.literal("unbinding monitor from removed keyboard."))
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape = when (state.getValue(HORIZONTAL_FACING)) {
		Direction.NORTH -> Companion.SHAPE_NORTH
		Direction.SOUTH -> Companion.SHAPE_SOUTH
		Direction.EAST -> Companion.SHAPE_EAST
		Direction.WEST -> Companion.SHAPE_WEST
		else -> Shapes.block()
	}

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HORIZONTAL_FACING, WATERLOGGED)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(HORIZONTAL_FACING, context.horizontalDirection.opposite)
			.setValue(WATERLOGGED, false)
}