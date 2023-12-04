package breadmod.item.advanced_item

import breadmod.gui.BreadModCreativeTab
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.food.FoodProperties
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class TestBreadItem : Item(Properties().food(FoodProperties.Builder().nutrition(6).build()).tab(BreadModCreativeTab)) {
    override fun use(pLevel: Level, pPlayer: Player, pHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (pHand == InteractionHand.MAIN_HAND && pLevel.isClientSide) {

        }
        return super.use(pLevel, pPlayer, pHand)
    }

    override fun appendHoverText(pStack: ItemStack, pLevel: Level?, pTooltipComponents: MutableList<Component>, pIsAdvanced: TooltipFlag) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced)
    }
}