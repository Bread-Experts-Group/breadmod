package breadmod.block.entity

import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pState: BlockState
) :
    BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pState), MenuProvider {
    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu {
        TODO("Not yet implemented")
    }

    override fun getDisplayName(): Component {
        TODO("Not yet implemented")
    }

}