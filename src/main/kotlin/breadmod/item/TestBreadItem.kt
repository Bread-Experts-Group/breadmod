package breadmod.item

import breadmod.BreadMod
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class TestBreadItem :
    Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).rarity(Rarity.EPIC)) {
    override fun finishUsingItem(pStack: ItemStack, pLevel: Level, pLivingEntity: LivingEntity): ItemStack {
        pLivingEntity.addEffect(MobEffectInstance(MobEffects.INVISIBILITY, 200, 0))
        return super.finishUsingItem(pStack, pLevel, pLivingEntity)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: List<Component>,
        pIsAdvanced: TooltipFlag
    ) {
//        pTooltipComponents.add(
//            1,
//            Component.translatable(("item." + BreadMod.ID) + ".test_bread.desc")
//                .withStyle(ChatFormatting.GOLD)
//        )
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
    }
}