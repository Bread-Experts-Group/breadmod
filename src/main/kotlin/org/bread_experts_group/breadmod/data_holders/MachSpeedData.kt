package org.bread_experts_group.breadmod.data_holders

import io.netty.buffer.ByteBuf
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource.AMBIENT
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation.ADD_VALUE
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.network.clientbound.MachTrailPacket
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

	fun tick(player: Player, slotId: Int) {
		if (slotId != 39) return
		val level = player.level() as? ServerLevel ?: return
		if (player.isSprinting) {
			this.sprintTimer++
			this.setMachStage()
			this.applySpeedBoost(player)
			if (player.attackAnim > 0f) this.reset(player)
			if (this.sprintTimer == 1) PacketDistributor.sendToPlayersTrackingChunk(
				level,
				player.chunkPosition(),
				MachTrailPacket(player.gameProfile)
			)
			val aabb = player.hitbox
			level.getEntities(player, aabb).forEach { target ->
				if (target is LivingEntity && !target.isDeadOrDying) {
					fun rand() = (target.random.nextDouble() - 0.5) * 1.1
					level.playSound(
						null,
						target.x, target.y, target.z,
						ModSounds.PUNCH.get(),
						AMBIENT
					)
					level.playSound(
						null,
						target.x, target.y, target.z,
						ModSounds.KILL_ENEMY.get(),
						AMBIENT
					)
					val vector = player.calculateViewVector(
						player.getViewXRot(0f),
						player.getViewYRot(0f)
					).multiply(5.0, 0.0, 5.0)
					target.addDeltaMovement(vector.add(0.0, 3.0, 0.0))
					target.kill()
					level.sendParticles(
						ParticleTypes.CLOUD,
						target.x, target.y, target.z,
						50,
						rand(), target.random.nextDouble(), rand(),
						0.5
					)
				}
			}
		} else {
			this.removeSpeedBoost(player)
		}
	}
}