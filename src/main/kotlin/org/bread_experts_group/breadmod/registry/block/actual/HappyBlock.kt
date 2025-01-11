package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.registry.entity.actual.PrimedHappyBlock
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3

class HappyBlock : TntBlock(Properties.ofFullCopy(Blocks.TNT)) {
	private fun BlockPos.adjust() = this.toVec3().plus(Vec3(0.5, 0.0, 0.5))
	override fun onCaughtFire(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		face: Direction?,
		igniter: LivingEntity?
	) {
		if (!level.isClientSide) {
			val primedHappyBlock = PrimedHappyBlock(level, pos.adjust(), owner = igniter, shouldSpread = true)
			level.addFreshEntity(primedHappyBlock)
			level.playSound(
				null,
				primedHappyBlock.x,
				primedHappyBlock.y,
				primedHappyBlock.z,
				ModSounds.HAPPY_BLOCK_FUSE.get(),
				SoundSource.BLOCKS,
				1.0f,
				1.0f
			)
			level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos)
		}
	}
}