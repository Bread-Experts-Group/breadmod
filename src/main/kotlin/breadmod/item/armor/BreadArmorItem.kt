package breadmod.item.armor

import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.*
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import kotlin.random.Random

// Exact timing not guaranteed. Each inventoryTick has a (100 / TICKS_PER_DECAY)% chance of damaging the armor piece by 1.
const val TICKS_PER_DECAY = 120
// Affects how far effects are transmitted by the bread armor (i.e. Strength I (full val. III) will be transmitted to players 4 * DISTANCE_MULTIPLIER blocks away)
const val DISTANCE_MULTIPLIER = 1.5

class BreadArmorItem(
    material: ArmorMaterial,
    slot: EquipmentSlot,
    properties: Properties,
): ArmorItem(material, slot, properties) {
    private val random = Random(material.hashCode())
    private fun ItemStack.hurt(amount: Int, player: ServerPlayer) = hurtAndBreak(amount, player) { it.broadcastBreakEvent(slot) }

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(pSlotId == slot.index && !pLevel.isClientSide && pEntity is ServerPlayer) {
            if(pEntity.isInLava) { pStack.hurt(Int.MAX_VALUE, pEntity); return }
            if(random.nextInt(1, TICKS_PER_DECAY) == 1 || pEntity.isInWater) pStack.hurt(if(pEntity.isUnderWater) 2 else 1, pEntity)

            val allEffects = mutableMapOf<MobEffect, Int>()
            val breadArmorEquipped = pEntity.armorSlots.count { stack ->
                PotionUtils.getCustomEffects(stack).forEach { allEffects.merge(it.effect, it.amplifier + 1, Integer::sum) }
                stack.item is BreadArmorItem
            }

            if(pEntity.foodData.foodLevel < breadArmorEquipped) pEntity.foodData.foodLevel = breadArmorEquipped * 2
            allEffects.forEach { (effect, amplifier) ->
                val appliedEffect = MobEffectInstance(effect, 200, 0, false, false)

                val distance = amplifier * DISTANCE_MULTIPLIER
                println(distance)
                val effectArea = AABB.ofSize(pEntity.eyePosition, distance, distance, distance)
                pLevel.getEntities(null, effectArea).forEach { if(it is LivingEntity) it.addEffect(appliedEffect) }
            }
        }
    }
}