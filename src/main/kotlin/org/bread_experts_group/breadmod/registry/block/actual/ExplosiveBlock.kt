package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.util.minus
import org.bread_experts_group.breadmod.util.toVec3
import java.util.function.BiConsumer

abstract class ExplosiveBlock(
	private val entity: (level: Level, pos: BlockPos, igniter: Entity?, delta: Vec3) -> PrimedTnt,
	private val sound: SoundEvent
) : TntBlock(Properties.ofFullCopy(Blocks.TNT)) {
	private fun prime(level: Level, pos: BlockPos, igniter: Entity?, delta: Vec3 = Vec3.ZERO) {
		if (level.isClientSide) return
		level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState())
		val primedBlock = this.entity(level, pos, igniter, delta)
		level.addFreshEntity(primedBlock)
		level.playSound(
			null,
			primedBlock.x,
			primedBlock.y,
			primedBlock.z,
			this.sound,
			SoundSource.BLOCKS,
			1.0f,
			1.0f
		)
		level.gameEvent(igniter, GameEvent.PRIME_FUSE, pos)
	}

	override fun onCaughtFire(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		face: Direction?,
		igniter: LivingEntity?
	): Unit = this.prime(level, pos, igniter)

	override fun onExplosionHit(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		explosion: Explosion,
		dropConsumer: BiConsumer<ItemStack, BlockPos>
	): Unit = this.prime(
		level, pos, explosion.indirectSourceEntity ?: explosion.directSourceEntity,
		pos.toVec3().let {
			it
				.minus(explosion.center())
				.scale((explosion.radius() / it.distanceTo(explosion.center())) * 0.1)
		}
	)
}