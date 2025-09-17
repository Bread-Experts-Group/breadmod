package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ItemParticleOption
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.client.sound.SpaceSoundInstance
import org.bread_experts_group.breadmod.util.entities
import org.bread_experts_group.breadmod.util.getStackInPlayerHand
import org.bread_experts_group.breadmod.util.itemTooltip
import org.bread_experts_group.breadmod.util.rayCast

class FineShineItem : Item(Properties()) {
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

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val interactionRange =
			player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE) ?: return super.use(level, player, usedHand)
		val entity = player.rayCast(interactionRange.value, entities())
		val stack = getStackInPlayerHand(player)

		entity?.let { result ->
			val pos = result.hitPosition
			val hit = result.hit

			if (hit is LivingEntity) {
				level.playSound(null, pos.x, pos.y, pos.z, SoundEvents.GENERIC_EAT, SoundSource.AMBIENT)
				hit.spawnItemParticles(stack, 8)
				if (!player.isCreative) stack.shrink(1)
				Thread.ofVirtual().start {
					Thread.sleep(4000)
					if (level.isClientSide) SpaceSoundInstance(hit).play()
					hit.addEffect(MobEffectInstance(MobEffects.LEVITATION, 8 * 20, 3))
					Thread.sleep(7500)
					if (!level.isClientSide) level.explode(
						hit,
						hit.x,
						hit.y,
						hit.z,
						5f,
						false,
						Level.ExplosionInteraction.MOB
					)
					hit.kill()
				}
			}
		}

		return super.use(level, player, usedHand)
	}
}