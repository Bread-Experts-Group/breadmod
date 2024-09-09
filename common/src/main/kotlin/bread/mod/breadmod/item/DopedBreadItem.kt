package bread.mod.breadmod.item

import bread.mod.breadmod.ModMainCommon.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

// todo data generation for multi layered item textures
class DopedBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build())) {
    override fun finishUsingItem(pStack: ItemStack, pLevel: Level, pLivingEntity: LivingEntity): ItemStack {
        // todo replace with PotionContents (probably have to add potions using createItemStack()
        //        PotionUtils.getCustomEffects(pStack).forEach { pLivingEntity.addEffect(it) }
        return super.finishUsingItem(pStack, pLevel, pLivingEntity)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(1, modTranslatable("item", "doped_bread", "tooltip").withStyle(ChatFormatting.GREEN))
//        PotionUtils.addPotionTooltip(PotionUtils.getCustomEffects(pStack), pTooltip, 1.0F)
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}