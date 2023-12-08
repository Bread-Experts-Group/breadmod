package breadmod.item.armor

import breadmod.BreadMod
import breadmod.gui.BreadModCreativeTab
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level


class BreadShieldItem : ShieldItem(Properties().tab(BreadModCreativeTab).stacksTo(1).durability(250)) {
    override fun isValidRepairItem(pToRepair: ItemStack, pRepair: ItemStack): Boolean = false

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        pTooltip.add(1,Component.translatable("item.${BreadMod.ID}.bread_shield.desc").withStyle(ChatFormatting.AQUA))
        super.appendHoverText(pStack, pLevel, pTooltip, pFlag)
    }
}