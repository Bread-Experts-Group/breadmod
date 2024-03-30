package breadmod.item.armor

import breadmod.ModConfiguration.DECAY_CHANCE_PER_TICK
import breadmod.ModConfiguration.EFFECT_DISTANCE_MULTIPLIER
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.*
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import kotlin.random.Random

class BreadArmorItem(
    material: ArmorMaterial,
    type: Type,
    properties: Properties,
): ArmorItem(material, type, properties) {
    private val random = Random(material.hashCode())
    private fun ItemStack.hurt(amount: Int, player: ServerPlayer) = hurtAndBreak(amount, player) { it.broadcastBreakEvent(type.slot) }

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(pSlotId == type.ordinal && !pLevel.isClientSide && pEntity is ServerPlayer) {
            if(pEntity.isInLava) { pStack.hurt(Int.MAX_VALUE, pEntity); return }
            if(random.nextInt(1, DECAY_CHANCE_PER_TICK.get()) == 1 || pEntity.isInWater) pStack.hurt(if(pEntity.isUnderWater) 2 else 1, pEntity)

            val allEffects = mutableMapOf<MobEffect, Int>()
            val breadArmorEquipped = pEntity.armorSlots.count { stack ->
                PotionUtils.getCustomEffects(stack).forEach { allEffects.merge(it.effect, it.amplifier + 1, Integer::sum) }
                stack.item is BreadArmorItem
            }

            if(pEntity.foodData.foodLevel < breadArmorEquipped) pEntity.foodData.foodLevel = breadArmorEquipped * 2
            allEffects.forEach { (effect, amplifier) ->
                val appliedEffect = MobEffectInstance(effect, 200, 0, false, false)

                val distance = amplifier * EFFECT_DISTANCE_MULTIPLIER.get()
                println(distance)
                val effectArea = AABB.ofSize(pEntity.eyePosition, distance, distance, distance)
                pLevel.getEntities(null, effectArea).forEach { if(it is LivingEntity) it.addEffect(appliedEffect) }
            }
        }
    }
}