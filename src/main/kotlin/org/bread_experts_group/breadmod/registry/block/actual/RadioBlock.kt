package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.client.gui.screens.RadioScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.playingSounds
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.RadioBlockEntity

class RadioBlock : BreadModBlockWithEntity(Properties.of()) {
	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (level.isClientSide) localClient.setScreen(RadioScreen(pos))
		return InteractionResult.sidedSuccess(level.isClientSide)
	}

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		if (level.isClientSide) {
			localClient.soundManager.stop(playingSounds[pos] ?: return)
			playingSounds.remove(pos)
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
		RadioBlockEntity(pos, state)

	override fun getBlockEntityType(level: Level, state: BlockState): BlockEntityType<*> =
		ModBlockEntityTypes.RADIO.get()

	override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

	override fun codec(): MapCodec<out BaseEntityBlock> = simpleCodec { this }
}