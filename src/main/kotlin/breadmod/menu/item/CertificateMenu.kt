package breadmod.menu.item

import breadmod.menu.AbstractModContainerMenu
import breadmod.registry.item.ModItems
import breadmod.registry.menu.ModMenuTypes
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

class CertificateMenu(
    pContainerId: Int,
    val inventory: Inventory,
) : AbstractModContainerMenu(
    ModMenuTypes.CERTIFICATE.get(),
    pContainerId
) {
//    init {
//        addInventorySlots(inventory, )
//    }

    override fun quickMoveStack(pPlayer: Player, pIndex: Int): ItemStack = ItemStack.EMPTY
    override fun stillValid(pPlayer: Player): Boolean = pPlayer.inventory.contains(ModItems.CERTIFICATE.get().defaultInstance)
}