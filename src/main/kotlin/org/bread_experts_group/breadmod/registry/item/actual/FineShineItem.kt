package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent
import org.bread_experts_group.breadmod.client.sound.SpaceSoundInstance
import org.bread_experts_group.breadmod.registry.item.IEntityInteractingItem
import org.bread_experts_group.breadmod.util.component1
import org.bread_experts_group.breadmod.util.component2
import org.bread_experts_group.breadmod.util.component3
import org.bread_experts_group.breadmod.util.itemTooltip

class FineShineItem : Item(Properties()), IEntityInteractingItem {
	private fun LivingEntity.spawnItemParticles(stack: ItemStack, amount: Int) {
		repeat(amount) {
			var vec3 = Vec3((this.random.nextFloat().toDouble() - 0.5) * 0.1, Math.random() * 0.1 + 0.1, 0.0)
			vec3 = vec3.xRot(-this.xRot * (Math.PI / 180.0).toFloat())
			vec3 = vec3.yRot(-this.yRot * (Math.PI / 180.0).toFloat())
			val d0 = (-this.random.nextFloat()).toDouble() * 0.6 - 0.3
			var vec31 = Vec3((this.random.nextFloat().toDouble() - 0.5) * 0.3, d0, 0.6)
			vec31 = vec31.xRot(-this.xRot * (Math.PI / 180.0).toFloat())
			vec31 = vec31.yRot(-this.yRot * (Math.PI / 180.0).toFloat())
			vec31 = vec31.add(this.x, this.eyeY, this.z)
			this.level().addParticle(
				ItemParticleOption(ParticleTypes.ITEM, stack),
				vec31.x,
				vec31.y,
				vec31.z,
				vec3.x,
				vec3.y + 0.05,
				vec3.z
			)
		}
	}

	override fun appendHoverText(
		stack: ItemStack,
		context: TooltipContext,
		tooltipComponents: MutableList<Component>,
		tooltipFlag: TooltipFlag
	) {
		tooltipComponents.add(this.itemTooltip().withStyle(ChatFormatting.ITALIC, ChatFormatting.AQUA))
	}

	override fun onInteractWithEntity(
		event: PlayerInteractEvent.EntityInteract,
		player: Player,
		target: Entity,
		level: Level,
		usedHand: InteractionHand,
		pos: BlockPos,
		stack: ItemStack
	) {
		val (x, y, z) = target.position()
		if (target is LivingEntity) {
			level.playSound(null, x, y, z, SoundEvents.GENERIC_EAT, SoundSource.AMBIENT)
			target.spawnItemParticles(stack, 8)
			if (!player.isCreative) stack.shrink(1)
			Thread.ofVirtual().start {
				Thread.sleep(4000)
				if (level.isClientSide) SpaceSoundInstance(target).play()
				target.addEffect(MobEffectInstance(MobEffects.LEVITATION, 8 * 20, 3))
				Thread.sleep(7500)
				if (!level.isClientSide) level.explode(
					target,
					target.x,
					target.y,
					target.z,
					5f,
					false,
					Level.ExplosionInteraction.MOB
				)
				target.kill()
			}
		}
	}
}