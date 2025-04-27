package org.bread_experts_group.breadmod.data_holders.common

import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource.AMBIENT
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.network.clientbound.MachTrailPacket
import org.bread_experts_group.breadmod.registry.ModDamageType
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.sound.ModSounds

data class MachSpeedData(
	var sprintTimer: Int = 0,
	var machStage: Int = 0
) {
	companion object {
		val STREAM_CODEC: StreamCodec<ByteBuf, MachSpeedData> = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, MachSpeedData::sprintTimer,
			::MachSpeedData
		)

		fun get(stack: ItemStack): MachSpeedData = stack.getOrDefault(ModDataComponents.MACH_SPEED, MachSpeedData())
	}

	private val speedModifiers: List<AttributeModifier> = buildList {
		repeat(4) { index ->
			this.add(AttributeModifier(BreadMod.modLocation("mach_speed_${index + 1}"), 0.08, ADD_VALUE))
		}
	}

	private fun applyIfNotPresent(modifierIndex: Int, player: Player) {
		val movementSpeed = player.attributes.getInstance(Attributes.MOVEMENT_SPEED) ?: return
		if (!movementSpeed.hasModifier(this.speedModifiers[modifierIndex].id))
			movementSpeed.addTransientModifier(this.speedModifiers[modifierIndex])
	}

	private fun removeIfPresent(modifierIndex: Int, player: Player) {
		val movementSpeed = player.attributes.getInstance(Attributes.MOVEMENT_SPEED) ?: return
		if (movementSpeed.hasModifier(this.speedModifiers[modifierIndex].id))
			movementSpeed.removeModifier(this.speedModifiers[modifierIndex])
	}

	private fun applySpeedBoost(player: Player): Unit = this.applyIfNotPresent(this.machStage - 1, player)
	private fun removeSpeedBoost(player: Player): Unit = repeat(4) { index -> this.removeIfPresent(index, player) }

	private fun setMachStage() {
		this.machStage = if (this.sprintTimer >= 70) 4
		else if (this.sprintTimer >= 40) 3
		else if (this.sprintTimer >= 20) 2
		else if (this.sprintTimer >= 1) 1
		else 0
	}

	fun reset(player: Player) {
		this.machStage = 0
		this.sprintTimer = 0
		this.removeSpeedBoost(player)
	}

	fun tick(player: Player, level: Level, slotId: Int) {
		if (slotId != 39) return
		if (player.isSprinting) {
			this.sprintTimer++
			this.setMachStage()
			this.applySpeedBoost(player)
			if (player.attackAnim > 0f) this.reset(player)
			if (level is ServerLevel && this.sprintTimer == 1) PacketDistributor.sendToPlayersTrackingChunk(
				level,
				player.chunkPosition(),
				MachTrailPacket(player.gameProfile)
			)
			if (this.machStage > 2) {
				val aabb = player.hitbox.inflate(0.15)
				level.getEntities(player, aabb).forEach { target ->
					if (target is LivingEntity && !target.isDeadOrDying) {
						fun rand() = (target.random.nextDouble() - 0.5) * 1.1
						val vector = player.calculateViewVector(
							player.getViewXRot(0f),
							player.getViewYRot(0f)
						).multiply(4.0, 0.0, 4.0)
						target.addDeltaMovement(vector.add(0.0, 2.5, 0.0))
						if (this.machStage >= 4) {
							target.hurt(ModDamageType.MACH.source(player.level()), 100f)
							this.playSound(target, ModSounds.PUNCH.get(), level)
							this.playSound(target, ModSounds.KILL_ENEMY.get(), level)
						} else {
							target.hurt(ModDamageType.MACH.source(player.level()), 5f)
							this.playSound(target, ModSounds.PUNCH.get(), level)
						}
						if (level is ServerLevel) level.sendParticles(
							ParticleTypes.CLOUD,
							target.x, target.y, target.z,
							50,
							rand(), target.random.nextDouble(), rand(),
							0.5
						)
					}
				}
			}
		} else {
			this.removeSpeedBoost(player)
		}
	}

	private fun playSound(target: LivingEntity, sound: SoundEvent, level: Level) =
		level.playSound(null, target.x, target.y, target.z, sound, AMBIENT)
}