package org.bread_experts_group.breadmod.experimental

import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.EventHooks
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
		val toDetonate: Set<BlockPos>,
		val hitEntities: Map<Entity, EntityExplosionResult>
	) {
		fun explode(source: Entity) {
			val damageSource = this.level.damageSources().explosion(source, source)
			this.level.gameEvent(source, GameEvent.EXPLODE, this.pos)
			this.hitEntities.forEach { entity, result ->
				entity.hurt(damageSource, result.damage)
				entity.addDeltaMovement(result.knockback)
			}
			this.toDetonate.forEach { blockPos ->
				this.level.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState())
			}
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
			for (j in 0 .. 15) {
				for (k in 0 .. 15) {
					for (l in 0 .. 15) {
						if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
							var d0 = (j.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
							var d1 = (k.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
							var d2 = (l.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
							val d3 = sqrt(d0 * d0 + d1 * d1 + d2 * d2)
							d0 /= d3
							d1 /= d3
							d2 /= d3
							var f: Float = radius * (0.7f + level.random.nextFloat() * 0.6f)
							var d4: Double = pos.x
							var d6: Double = pos.y
							var d8: Double = pos.z
							while (f > 0.0f) {
								val blockpos = BlockPos.containing(d4, d6, d8)
								val blockstate: BlockState = level.getBlockState(blockpos)
								level.getFluidState(blockpos)
								if (!level.isInWorldBounds(blockpos)) break
//								this@BreadModExplosion.calculator.getBlockExplosionResistance(
//									simExplosion,
//									level,
//									blockpos,
//									blockstate,
//									fluidstate
//								).ifPresent { f -= (it + 0.3f) * 0.3f }
								if (f > 0.0f && this@BreadModExplosion.calculator.shouldBlockExplode(
										simExplosion,
										level,
										blockpos,
										blockstate,
										f
									)
								) this.add(blockpos)

								d4 += d0 * 0.3
								d6 += d1 * 0.3
								d8 += d2 * 0.3
								f -= 0.22500001f
							}
						}
					}
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
		return ExplosionResults(level, pos, toDetonate, hitEntities)
	}
}