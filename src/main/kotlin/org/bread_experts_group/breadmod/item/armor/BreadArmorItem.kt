package org.bread_experts_group.breadmod.item.armor

import net.minecraft.ChatFormatting
import net.minecraft.core.Holder
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.alchemy.Potions
import net.minecraft.world.item.component.DyedItemColor
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.ModConfiguration.COMMON
import java.awt.Color
import java.text.DecimalFormat
import kotlin.random.Random

class BreadArmorItem(type: Type) : ArmorItem(
    ModArmorMaterials.BREAD,
    type,
    Properties()
        .component(DataComponents.POTION_CONTENTS, PotionContents(Potions.SWIFTNESS))
        .component(DataComponents.DYED_COLOR, DyedItemColor(Color(216, 196, 170).rgb, false))
        .stacksTo(1)
) {
    private val random = Random(material.hashCode())

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        val potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents(Potions.SWIFTNESS))
        if (potion.customEffects.isEmpty() || potion.customEffects.first() == null) return
        // todo NoSuchElementException thrown at potion.customEffects
        val range = distanceMultiplier.get() * potion.customEffects.first().amplifier
        tooltipComponents.add(
            modTranslatable(
                "item", "bread_armor", "range",
                args = listOf(decimalFormat.format(range), if (range == 1.0) "block" else "blocks")
            ).withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC)
        )
        potion.addPotionTooltip(tooltipComponents::add, 1.0f, context.tickRate())
    }

    /*
        override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) = PotionUtils.addPotionTooltip(
        PotionUtils.getCustomEffects(pStack).map {
            val range = distanceMultiplier.get() * it.amplifier
            pTooltip.add(
            modTranslatable(
                "item", "bread_armor", "range",
                args = listOf(decimalFormat.format(range), if(range == 1.0) "block" else "blocks")
            ).withStyle { style -> style.withColor(ChatFormatting.BLUE); style.withItalic(true) })
            MobEffectInstance(it.effect, -1, 0)
        },
        pTooltip,
        1.0F
    )
     */

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (type.slot.index == slotId && level is ServerLevel && entity is ServerPlayer) {
            val potion = stack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)

            if (entity.isInLava) stack.hurtAndBreak(Int.MAX_VALUE, entity, type.slot)
            if (random.nextInt(1, decayChance.get()) == 1 || entity.isInWaterOrRain
            ) stack.hurtAndBreak(if (entity.isUnderWater) 2 else 1, entity, type.slot)

            val allEffects = mutableMapOf<MobEffect, Int>()
            val breadArmorEquipped = entity.armorSlots.count {
                potion.customEffects.forEach { allEffects.merge(it.effect.value(), it.amplifier + 1, Integer::sum) }
                stack.item is BreadArmorItem
            }

            if (entity.foodData.foodLevel < breadArmorEquipped) entity.foodData.foodLevel = breadArmorEquipped * 2

            allEffects.forEach { (effect, amplifier) ->
                val appliedEffect = MobEffectInstance(Holder.direct(effect), 200, 0, false, false)
                val distance = distanceMultiplier.get() * amplifier
                val effectArea = AABB.ofSize(entity.eyePosition, distance, distance, distance)
                level.getEntities(null, effectArea).forEach { if (it is Player) it.addEffect(appliedEffect) }
            }
        }
    }

    companion object {
        val distanceMultiplier = COMMON.EFFECT_DISTANCE_MULTIPLIER
        val decayChance = COMMON.DECAY_CHANCE_PER_TICK

        private val decimalFormat = DecimalFormat("0.#")
        val BREAD_COLOR = Color(216, 196, 170)
    }
}