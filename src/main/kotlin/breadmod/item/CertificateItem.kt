package breadmod.item

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.item.menu.CertificateMenu
import breadmod.registry.item.ModItems
import net.minecraft.client.renderer.item.ItemProperties
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraftforge.network.NetworkHooks

class CertificateItem: Item(Properties().stacksTo(16)), MenuProvider {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = pPlayer.getItemInHand(pUsedHand)
        if(pLevel.isClientSide) return InteractionResultHolder.pass(stack)
        val dyeSlot = pPlayer.inventory.findSlotMatchingItem(DYE)
        if(dyeSlot > 0) {
            NetworkHooks.openScreen(pPlayer as ServerPlayer, this, pPlayer.blockPosition())
        }
        return InteractionResultHolder.fail(stack)
    }

    override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        CertificateMenu(pContainerId)

    override fun getDisplayName(): Component = modTranslatable("item", "gui", "certificate")

    companion object {
        val DYE = ItemStack(Items.BLUE_DYE)
    }
}