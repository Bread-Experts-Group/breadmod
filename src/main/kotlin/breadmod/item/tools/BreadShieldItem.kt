package breadmod.item.tools

import breadmod.BreadMod
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class BreadShieldItem : ShieldItem(Properties().stacksTo(1).durability(250)) {

    override fun isValidRepairItem(pToRepair: ItemStack, pRepair: ItemStack): Boolean {
        return super.isValidRepairItem(pToRepair, pRepair)
    }

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