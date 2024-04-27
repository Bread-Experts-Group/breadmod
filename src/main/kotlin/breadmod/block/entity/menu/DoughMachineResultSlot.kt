package breadmod.block.entity.menu

import net.minecraft.world.item.ItemStack
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

class DoughMachineResultSlot(itemHandler: IItemHandler?, index: Int, xPosition: Int, yPosition: Int) :
    SlotItemHandler(itemHandler, index, xPosition, yPosition) {

    override fun mayPlace(stack: ItemStack): Boolean {
        return false
    }
}