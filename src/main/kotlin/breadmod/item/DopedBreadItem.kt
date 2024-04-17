package breadmod.item

import breadmod.BreadMod.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.level.Level
import net.minecraftforge.common.ForgeHooks
import java.util.function.Consumer

class DopedBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build())) {
//    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
//        if(pEntity is LivingEntity) pEntity.addEffect(MobEffectInstance(MobEffects.INVISIBILITY,200,0))
//    }

    private fun listPotionEffects(pStack: ItemStack, pOutput: Consumer<MobEffectInstance>) {
        val compoundTag = pStack.tag
        if (compoundTag != null && compoundTag.contains("Effects", 9)) {
            val listTag = compoundTag.getList("Effects", 10)

            for (i in listTag.indices) {
                val compoundTag1 = listTag.getCompound(i)
                val j = if (compoundTag1.contains("EffectDuration", 99)) {
                    compoundTag1.getInt("EffectDuration")
                } else {
                    160
                }

                var mobeffect = MobEffect.byId(compoundTag1.getInt("EffectId"))
                mobeffect = ForgeHooks.loadMobEffect(compoundTag1, "forge:effect_id", mobeffect)
                if (mobeffect != null) {
                    pOutput.accept(MobEffectInstance(mobeffect, j))
                }
            }
        }
    }

    override fun finishUsingItem(pStack: ItemStack, pLevel: Level, pLivingEntity: LivingEntity): ItemStack {
        val itemStack = super.finishUsingItem(pStack, pLevel, pLivingEntity)
        listPotionEffects(itemStack, pLivingEntity::addEffect)
        return super.finishUsingItem(pStack, pLevel, pLivingEntity)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        pTooltip.add(1,
            modTranslatable("item", "doped_bread", "tooltip")
                .withStyle(ChatFormatting.GREEN)
        )
        val list: List<MobEffectInstance> = ArrayList()
        listPotionEffects(pStack) { e: MobEffectInstance? -> list.contains(e) }
        PotionUtils.addPotionTooltip(list, pTooltip, 1.0F)
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}