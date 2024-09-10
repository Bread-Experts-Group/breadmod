package bread.mod.breadmod.item

import bread.mod.breadmod.ModMainCommon.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ShieldItem
import net.minecraft.world.item.TooltipFlag

class BreadShieldItem : ShieldItem(Properties().stacksTo(1).durability(250)) {
    override fun isValidRepairItem(pToRepair: ItemStack, pRepair: ItemStack): Boolean {
        return super.isValidRepairItem(pToRepair, pRepair)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            modTranslatable("item", "bread_shield", "tooltip")
                .withStyle(ChatFormatting.AQUA)
        )
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag)
    }
}