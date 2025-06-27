package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance

class RadioBlock : Block(Properties.of()) {
	override fun useWithoutItem(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		player: Player,
		hitResult: BlockHitResult
	): InteractionResult {
		if (level.isClientSide) {
			val instance = StereoSoundInstance(this::class.java.getResourceAsStream("/chicken.wav")!!, pos, 50.0)
			localClient.soundManager.play(instance)
		}
//		if (!level.isClientSide) PacketDistributor.sendToPlayersTrackingChunk(
//			level as ServerLevel,
//			ChunkPos(pos),
//			SoundPacket(ModSounds.GOING_UP.delegate, pos, 50.0)
//		)
		return InteractionResult.sidedSuccess(level.isClientSide)
	}
}