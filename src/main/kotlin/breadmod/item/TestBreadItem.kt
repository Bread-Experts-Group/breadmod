package breadmod.item

import breadmod.BreadMod.modTranslatable
import breadmod.util.raycast
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.EntityHitResult

class TestBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).rarity(Rarity.EPIC)) {
    override fun finishUsingItem(pStack: ItemStack, pLevel: Level, pLivingEntity: LivingEntity): ItemStack {
        when(val hit = raycast(pLivingEntity, 40, false)) {
            is BlockHitResult -> {
                pLevel.explode(pLivingEntity, hit.blockPos.x.toDouble(), hit.blockPos.y.toDouble(), hit.blockPos.z.toDouble(), 50f, true, Level.ExplosionInteraction.TNT)
            }
            is EntityHitResult -> {
                hit.entity.kill()
            }
        }

        return super.finishUsingItem(pStack, pLevel, pLivingEntity)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(1,
            modTranslatable("item", "test_bread", "tooltip")
                .withStyle(ChatFormatting.GOLD)
        )
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
    }
}