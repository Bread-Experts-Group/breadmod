package breadmod.item.menu

import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack

class CertificateMenu(
    pContainerId: Int
) : AbstractContainerMenu(
    ModMenuTypes.CERTIFICATE.get(),
    pContainerId
) {
    override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(pPlayer: Player): Boolean = pPlayer.inventory.contains(ModItems.CERTIFICATE.get().defaultInstance)
}