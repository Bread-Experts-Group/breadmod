package breadmod.item

import breadmod.ModMain.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.level.Level

class DopedBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build())) {
    override fun finishUsingItem(pStack: ItemStack, pLevel: Level, pLivingEntity: LivingEntity): ItemStack {
        PotionUtils.getCustomEffects(pStack).forEach { pLivingEntity.addEffect(it) }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        pTooltip.add(1, modTranslatable("item", "doped_bread", "tooltip").withStyle(ChatFormatting.GREEN))
        PotionUtils.addPotionTooltip(PotionUtils.getCustomEffects(pStack), pTooltip, 1.0F)
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}