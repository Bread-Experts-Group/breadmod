package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.clientbound.SoundPacket
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class RadioBlock : Block(Properties.of()) {
	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (!level.isClientSide) PacketDistributor.sendToPlayersTrackingChunk(
			level as ServerLevel,
			ChunkPos(pos),
			SoundPacket(ModSounds.GOING_UP.delegate, pos, 50.0)
		)
		return super.useWithoutItem(state, level, pos, player, hitResult)
	}
}