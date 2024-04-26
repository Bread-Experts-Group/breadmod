package breadmod.block.entity.menu

import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.ItemStack

class DoughMachineMenu(pMenuType: MenuType<*>, pContainerId: Int) : AbstractContainerMenu(pMenuType, pContainerId) {

        override fun quickMoveStack(p0: Player, p1: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun stillValid(p0: Player): Boolean {
        TODO("Not yet implemented")
    }
}