package org.bread_experts_group.breadmod.experimental

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.EventHooks
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.clientbound.SpreadParticlesPacket
import org.joml.Math
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import kotlin.math.sqrt

object BreadModExplosion {
	val calculator: ExplosionDamageCalculator = ExplosionDamageCalculator()

	data class EntityExplosionResult(
		val damage: Float,
		val knockback: Vec3
	)

	data class ExplosionResults(
		val level: Level,
		val pos: Vec3,
		val radius: Float,
		val toDetonate: Set<BlockPos>,
		val hitEntities: Map<Entity, EntityExplosionResult>,
		private val simExplosion: Explosion
	) {
		fun explode(source: Entity?) {
			if (this.level.isClientSide) throw IllegalStateException("BreadModExplosion must not be on the client!")
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
					this.radius, this.radius.toInt()
				)
			)
			val damageSource = this.level.damageSources().explosion(source, source)
			this.level.gameEvent(source, GameEvent.EXPLODE, this.pos)
			this.hitEntities.forEach { entity, result ->
				entity.hurt(damageSource, result.damage)
				entity.addDeltaMovement(result.knockback)
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
	val fibonacciSpherePoints: Set<Vec3> = buildSet<Vec3> {
		val phi = Math.PI * (Math.sqrt(5.0) - 1.0)
		val samples = 1000 // Move this to a configuration option?
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

	fun calculate(
		level: Level,
		pos: Vec3,
		radius: Float,
	): ExplosionResults {
		val simExplosion = Explosion(
			level, null, pos.x, pos.y, pos.z, radius,
			true, Explosion.BlockInteraction.DESTROY
		)
		val toDetonate = buildSet {
			this@BreadModExplosion.fibonacciSpherePoints.forEach { direction ->
				var currentPos = pos
				var power = radius
				for (@Suppress("unused") depth in 0 until radius.toInt()) {
					val thisBlockPos = BlockPos(currentPos.toVec3i())
					val blockState = level.getBlockState(thisBlockPos)
					if (blockState.isAir) {
						power -= 0.75f
					} else {
						this@BreadModExplosion.calculator.getBlockExplosionResistance(
							simExplosion,
							level,
							thisBlockPos,
							blockState,
							Fluids.EMPTY.defaultFluidState()
						).ifPresent { power -= (it + 0.3f) * 0.3f }
						if (power < 0f) break
						this.add(thisBlockPos)
					}
					currentPos = currentPos.add(direction.offsetRandom(level.random, 0.5f))
				}
			}
		}
		val f2: Float = radius * 2.0f
		val k1 = Mth.floor(pos.x - f2.toDouble() - 1.0)
		val l1 = Mth.floor(pos.x + f2.toDouble() + 1.0)
		val i2 = Mth.floor(pos.y - f2.toDouble() - 1.0)
		val i1 = Mth.floor(pos.y + f2.toDouble() + 1.0)
		val j2 = Mth.floor(pos.z - f2.toDouble() - 1.0)
		val j1 = Mth.floor(pos.z + f2.toDouble() + 1.0)
		val list: MutableList<Entity> = level.getEntities(
			null,
			AABB(
				k1.toDouble(), i2.toDouble(), j2.toDouble(),
				l1.toDouble(), i1.toDouble(), j1.toDouble()
			)
		)
		EventHooks.onExplosionDetonate(level, simExplosion, list, f2.toDouble())
		val hitEntities = mutableMapOf<Entity, EntityExplosionResult>()
		for (entity in list) {
			if (entity is Player && (entity.isSpectator || entity.isCreative || entity.abilities.flying)) continue
			if (!entity.ignoreExplosion(simExplosion)) {
				val d11 = sqrt(entity.distanceToSqr(pos)) / f2.toDouble()
				if (d11 <= 1.0) {
					var d5: Double = entity.x - pos.x
					var d7: Double = (if (entity is PrimedTnt) entity.y else entity.eyeY) - pos.y
					var d9: Double = entity.z - pos.z
					val d12 = sqrt(d5 * d5 + d7 * d7 + d9 * d9)
					if (d12 == 0.0) continue
					d5 /= d12
					d7 /= d12
					d9 /= d12
					val d13 = (1.0 - d11) * Explosion.getSeenPercent(pos, entity)
						.toDouble() * this.calculator.getKnockbackMultiplier(entity).toDouble()
					if (entity is LivingEntity)
						d13 * (1.0 - entity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE))
					else d13

					d5 *= d13
					d7 *= d13
					d9 *= d13
					hitEntities[entity] = EntityExplosionResult(
						if (this.calculator.shouldDamageEntity(simExplosion, entity))
							this.calculator.getEntityDamageAmount(simExplosion, entity) else 0f,
						EventHooks.getExplosionKnockback(
							level, simExplosion, entity,
							Vec3(d5, d7, d9)
						)
					)
				}
			}
		}
		return ExplosionResults(level, pos, radius, toDetonate, hitEntities, simExplosion)
	}
}