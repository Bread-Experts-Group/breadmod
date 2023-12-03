package breadmod.item.advanced_item

import breadmod.gui.BreadModCreativeTab
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class TestShieldItem : ShieldItem(Properties().tab(BreadModCreativeTab)) {
    override fun getUseAnimation(pStack: ItemStack): UseAnim {
        return UseAnim.BLOCK
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        return pTooltip.add(1,Component.literal("test").withStyle(ChatFormatting.GRAY))
//        super.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}