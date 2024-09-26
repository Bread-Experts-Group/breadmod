package bread.mod.breadmod.item.armor

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.alchemy.PotionContents
import net.minecraft.world.item.component.DyedItemColor
import org.apache.logging.log4j.LogManager
import java.awt.Color

class BreadArmorItem(type: Type) : ArmorItem(
    ModArmorMaterials.BREAD, type, Properties()
        .component(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
        .component(DataComponents.DYED_COLOR, DyedItemColor(Color(216, 196, 170).rgb, false))
        .stacksTo(1)
) {
    private val logger = LogManager.getLogger()

    // todo potion effects can be added and read with components, just gotta figure out how
//    init {
//        ItemStack(this).set(DataComponents.POTION_CONTENTS, PotionContents(Potions.LUCK))
//        PotionContents.createItemStack(this, Potions.LUCK)

//        this.components().forEach {
//            logger.info("Component listing: ${it.type}, ${it.value}")
//        }
//    }

    // todo go through all this and update it to work
    /*
    *     override val defaultTint: Color = Color(216, 196, 170)

    private val random = Random(material.hashCode())
    private fun ItemStack.hurt(amount: Int, entity: LivingEntity) = hurtAndBreak(amount, entity) {
    * it.broadcastBreakEvent(type.slot)
    * }

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

    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(type.slot.index == pSlotId && pLevel is ServerLevel && pEntity is ServerPlayer) {
            if(pEntity.isInLava) { pStack.hurt(Int.MAX_VALUE, pEntity); return }
            if(random.nextInt(1, decayChance.get()) == 1 || pEntity.isInWater)
            * pStack.hurt(if(pEntity.isUnderWater) 2 else 1, pEntity)

            val allEffects = mutableMapOf<MobEffect, Int>()
            val breadArmorEquipped = pEntity.armorSlots.count { stack ->
                PotionUtils.getCustomEffects(stack).forEach { allEffects.merge(it.effect, it.amplifier + 1, Integer::sum) }
                stack.item is BreadArmorItem
            }

            if(pEntity.foodData.foodLevel < breadArmorEquipped) pEntity.foodData.foodLevel = breadArmorEquipped * 2

            allEffects.forEach { (effect, amplifier) ->
                val appliedEffect = MobEffectInstance(effect, 200, 0, false, false)

                val distance = distanceMultiplier.get() * amplifier
                val effectArea = AABB.ofSize(pEntity.eyePosition, distance, distance, distance)
                pLevel.getEntities(null, effectArea).forEach { if(it is LivingEntity) it.addEffect(appliedEffect) }
            }
        }
    }

    companion object {
        val distanceMultiplier = COMMON.EFFECT_DISTANCE_MULTIPLIER
        val decayChance = COMMON.DECAY_CHANCE_PER_TICK

        private val decimalFormat = DecimalFormat("0.#")
        val BREAD_COLOR = Color(216, 196, 170)
    }
    * */
}