package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item.TooltipContext
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay
import org.bread_experts_group.breadmod.command.server.WarTimerCommand.increaseTime
import org.bread_experts_group.breadmod.data_holders.server.WarTimerData
import org.bread_experts_group.breadmod.data_holders.server.WarTimerData.Companion.warTimerMap
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerIncrement
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.util.combine
import org.bread_experts_group.breadmod.util.east
import org.bread_experts_group.breadmod.util.south
import org.bread_experts_group.breadmod.util.west
import java.util.stream.Stream

class WarTerminalBlock : BreadModBlock(Properties.of()) {
	private companion object {
		val SHAPE_NORTH: VoxelShape = Stream.of(
			box(0.0, 6.0, 0.0, 16.0, 7.0, 1.0),
			box(0.0, 0.0, 1.0, 16.0, 7.0, 5.0),
			box(0.0, 0.0, 5.0, 16.0, 16.0, 16.0)
		).combine()
		val SHAPE_SOUTH: VoxelShape = this.SHAPE_NORTH.south()
		val SHAPE_EAST: VoxelShape = this.SHAPE_NORTH.east()
		val SHAPE_WEST: VoxelShape = this.SHAPE_NORTH.west()
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun playerWillDestroy(
		level: Level,
		pos: BlockPos,
		state: BlockState,
		thisPlayer: Player
	): BlockState {
		val server = level.server ?: return super.playerWillDestroy(level, pos, state, thisPlayer)
		server.playerList.players.forEach { player ->
			val check = warTimerMap[player]
			if (check != null) {
				if (check.active) increaseTime(player, check, 30)
			} else {
				warTimerMap[player] = WarTimerData(timeLeft = 0)
				val data = warTimerMap[player] ?: return@forEach
				WarOverlay.timeLeft = data.timeLeft
				data.increaseTime += 30
				PacketDistributor.sendToPlayer(player, WarTimerIncrement(true, data.increaseTime))
				PacketDistributor.sendToPlayer(player, WarTimerToggle(true))
			}
		}
		return super.playerWillDestroy(level, pos, state, thisPlayer)
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape = when (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
		Direction.SOUTH -> Companion.SHAPE_SOUTH
		Direction.EAST -> Companion.SHAPE_EAST
		Direction.WEST -> Companion.SHAPE_WEST
		else -> Companion.SHAPE_NORTH
	}

	override fun appendHoverTextAdditional(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(
			BreadMod.modTranslatable("block", "war_terminal", "tooltip").withStyle(ChatFormatting.RED)
		)
	}
}