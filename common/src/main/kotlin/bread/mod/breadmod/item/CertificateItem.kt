package bread.mod.breadmod.item

import bread.mod.breadmod.ModMainCommon.modTranslatable
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class CertificateItem: Item(Properties().stacksTo(16).rarity(Rarity.RARE)), MenuProvider {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = pPlayer.getItemInHand(pUsedHand)
        if(pLevel.isClientSide) return InteractionResultHolder.pass(stack)
        val dyeSlot = pPlayer.inventory.findSlotMatchingItem(DYE)
        if(dyeSlot > 0 || pPlayer.isCreative) {
//            NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
        }
        return InteractionResultHolder.fail(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(modTranslatable("item", "certificate", "description"))
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
//        CertificateMenu(pContainerId, pPlayerInventory)
        TODO()

    override fun getDisplayName(): Component = modTranslatable("item", "gui", "certificate")

    companion object {
        val DYE = ItemStack(Items.BLUE_DYE)
    }
}