package org.bread_experts_group.breadmod.util

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.clientbound.SpreadParticlesPacket
import org.bread_experts_group.breadmod.registry.ModDamageType
import org.joml.Math
import java.util.function.Function
import kotlin.random.Random

object BreadModExplosion {
	val calculator: ExplosionDamageCalculator = ExplosionDamageCalculator()

	data class ExplosionResults(
		val level: Level,
		val pos: Vec3,
		val radius: Float,
		val toDetonate: Set<BlockPos>,
		val hitEntities: Map<Entity, Float>,
		private val simExplosion: Explosion
	) {
		fun explode(source: Entity?) {
			check(!this.level.isClientSide) { "BreadModExplosion must not be on the client!" }
			val random = this.level.random
			this.level.playSound(
				null,
				this.pos.x, this.pos.y, this.pos.z,
				SoundEvents.GENERIC_EXPLODE.value(),
				SoundSource.BLOCKS,
				this.radius,
				(1.0f + random.nextFloat() * 0.2f) * 0.7f
			)
			PacketDistributor.sendToAllPlayers(
				SpreadParticlesPacket(
					this.level, ParticleTypes.EXPLOSION_EMITTER,
					this.pos.toVector3f(),
					this.radius
				)
			)
			this.level.gameEvent(source, GameEvent.EXPLODE, this.pos)
			val damageSourceLow = this.level.damageSources().explosion(source, source)
			val damageSourceHigh = ModDamageType.Companion.EXPLOSION_DAMAGE_HIGH.source(this.level)
			val damageSourceVeryHigh = ModDamageType.Companion.EXPLOSION_DAMAGE_VERY_HIGH.source(this.level)
			this.hitEntities.forEach { entity, rayPower ->
				entity.hurt(
					when (rayPower) {
						in 0f .. 500f     -> damageSourceLow
						in 501f .. 10000f -> damageSourceHigh
						else              -> damageSourceVeryHigh
					},
					rayPower
				)
			}
			this.toDetonate.forEach { blockPos ->
				this.level
					.getBlockState(blockPos)
					.onExplosionHit(this.level, blockPos, this.simExplosion)
					{ stack, pos -> Block.popResource(this.level, pos, stack) }
			}
		}
	}

	// THANK YOU Fnord @ https://stackoverflow.com/a/26127012
	// https://arxiv.org/pdf/0912.4540
	val getPoints: Function<Int, Set<Vec3>> = Util.memoize { count: Int ->
		buildSet<Vec3> {
			val phi = Math.PI * (Math.sqrt(5.0) - 1.0)
			val samples = count // Move this to a configuration option?
			repeat(samples) { i ->
				val y = 1.0 - (i / (samples - 1.0)) * 2.0
				val radius = Math.sqrt(1.0 - y * y)
				val theta = phi * i
				this.add(
					Vec3(
						Math.cos(theta) * radius,
						y,
						Math.sin(theta) * radius
					)
				)
			}
		}
	}

	fun calculate(
		level: Level,
		pos: Vec3,
		radius: Float,
		points: Int
	): ExplosionResults {
		val simExplosion = Explosion(
			level, null, pos.x, pos.y, pos.z, radius,
			true, Explosion.BlockInteraction.DESTROY
		)
		val hitEntities = mutableMapOf<Entity, Float>()
		val toDetonate = buildSet {
			val currentBlockPos = BlockPos.MutableBlockPos()
			this@BreadModExplosion.getPoints.apply(points).forEach { direction ->
				var currentPos = pos
				var power = radius
				val localTracerAttenuate = (Random.Default.nextFloat() * 3.0f) + 0.5f
				for (@Suppress("unused") depth in 0 until radius.toInt()) {
					currentBlockPos.set(currentPos.x, currentPos.y, currentPos.z)
					val blockState = level.getBlockState(currentBlockPos)
					val offset = if (blockState.isAir) {
						0.75f
					} else {
						val calculated = this@BreadModExplosion.calculator.getBlockExplosionResistance(
							simExplosion,
							level,
							currentBlockPos,
							blockState,
							Fluids.EMPTY.defaultFluidState()
						)
						if (calculated.isEmpty) 0.75f else ((calculated.get() + 0.3f) * 0.3f)
					}
					power -= offset + localTracerAttenuate
					if (power < 0f) break
					if (!blockState.isAir) this.add(currentBlockPos.immutable())
					val entities = level.getEntities(null, AABB.ofSize(currentPos, 1.0, 1.0, 1.0))
					entities.forEach { entity ->
						hitEntities[entity] = hitEntities.getOrPut(entity) { 0f } + power
					}
					currentPos = currentPos.add(direction.offsetRandom(level.random, offset))
				}
			}
		}
		return ExplosionResults(level, pos, radius, toDetonate, hitEntities, simExplosion)
	}
}