package breadmod.item

import breadmod.ModMain.modTranslatable
import breadmod.item.menu.CertificateMenu
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.*
import net.minecraft.world.level.Level
import net.minecraftforge.network.NetworkHooks

// todo custom mod rarity with any color support
class CertificateItem: Item(Properties().stacksTo(16).rarity(Rarity.RARE)), MenuProvider {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = pPlayer.getItemInHand(pUsedHand)
        if(pLevel.isClientSide) return InteractionResultHolder.pass(stack)
        val dyeSlot = pPlayer.inventory.findSlotMatchingItem(DYE)
        if(dyeSlot > 0 || pPlayer.isCreative) {
            NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
        }
        return InteractionResultHolder.fail(stack)
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: Level?,
        pTooltipComponents: MutableList<Component>,
        pIsAdvanced: TooltipFlag
    ) {
        pTooltipComponents.add(modTranslatable("item", "certificate", "description"))
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        CertificateMenu(pContainerId)

    override fun getDisplayName(): Component = modTranslatable("item", "gui", "certificate")

    companion object {
        val DYE = ItemStack(Items.BLUE_DYE)
    }
}