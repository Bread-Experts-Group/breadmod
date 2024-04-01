package breadmod.item.armor

import breadmod.ModConfiguration.COMMON
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import java.awt.Color
import kotlin.random.Random

class BreadArmorItem(
    material: ArmorMaterial,
    type: Type,
    properties: Properties,
): ArmorItem(material, type, properties) {
    val currentColor = Color(216, 196, 170)

    private val random = Random(material.hashCode())
    private fun ItemStack.hurt(amount: Int, entity: LivingEntity) = hurtAndBreak(amount, entity) { it.broadcastBreakEvent(type.slot) }

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(type.slot.index == pSlotId && pLevel is ServerLevel && pEntity is ServerPlayer) {
            if(pEntity.isInLava) { pStack.hurt(Int.MAX_VALUE, pEntity); return }
            if(random.nextInt(1, COMMON.DECAY_CHANCE_PER_TICK.get()) == 1 || pEntity.isInWater) pStack.hurt(if(pEntity.isUnderWater) 2 else 1, pEntity)

            val allEffects = mutableMapOf<MobEffect, Int>()
            val breadArmorEquipped = pEntity.armorSlots.count { stack ->
                PotionUtils.getCustomEffects(stack).forEach { allEffects.merge(it.effect, it.amplifier + 1, Integer::sum) }
                stack.item is BreadArmorItem
            }

            if(pEntity.foodData.foodLevel < breadArmorEquipped)
                pEntity.foodData.foodLevel = breadArmorEquipped * 2

            allEffects.forEach { (effect, amplifier) ->
                val appliedEffect = MobEffectInstance(effect, 200, 0, false, false)

                val distance = amplifier * COMMON.EFFECT_DISTANCE_MULTIPLIER.get()
                val effectArea = AABB.ofSize(pEntity.eyePosition, distance, distance, distance)
                pLevel.getEntities(null, effectArea).forEach { if(it is LivingEntity) it.addEffect(appliedEffect) }
            }
        }
    }
}