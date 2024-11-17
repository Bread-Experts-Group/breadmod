package org.bread_experts_group.breadmod.item

import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.menu.item.CertificateMenu

class CertificateItem : Item(Properties().stacksTo(16).rarity(Rarity.RARE)), MenuProvider {
    companion object {
        val DYE = ItemStack(Items.BLUE_DYE)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)
        if (level.isClientSide) return InteractionResultHolder.pass(stack)
        val dyeSlot = player.inventory.findSlotMatchingItem(DYE)
        if (dyeSlot > 0 || player.isCreative) player.openMenu(this, player.blockPosition())
        return InteractionResultHolder.fail(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(modTranslatable("item", "certificate", "tooltip"))
    }

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        CertificateMenu(containerId, playerInventory)

    override fun getDisplayName(): Component = modTranslatable("item", "certificate")
}