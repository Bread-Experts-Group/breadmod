package breadmod.item

import breadmod.BreadMod.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class DopedBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build())) {
    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
        if(pEntity is LivingEntity) pEntity.addEffect(MobEffectInstance(MobEffects.INVISIBILITY,200,0))
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
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}