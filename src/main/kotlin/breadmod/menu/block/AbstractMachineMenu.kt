package breadmod.menu.block

import breadmod.block.entity.machine.AbstractMachineBlockEntity
import breadmod.menu.AbstractModContainerMenu
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.util.capability.EnergyBattery
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities
import kotlin.jvm.optionals.getOrNull

abstract class AbstractMachineMenu<T : AbstractMachineBlockEntity.Progressive<T, R>, R : FluidEnergyRecipe>(
    pMenuType: MenuType<*>,
    pContainerId: Int,
    protected val inventory: Inventory,
    val parent: T,
    hotBarY: Int,
    inventoryY: Int
) : AbstractModContainerMenu(pMenuType, pContainerId) {
    open fun getScaledProgress(): Int =
        parent.currentRecipe.getOrNull()?.let { ((parent.progress.toFloat() / it.time) * 14).toInt() } ?: 0

    open fun getEnergyStoredScaled(): Int =
        parent.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)
            ?.let { ((it.energyStored.toFloat() / it.maxEnergyStored) * 47).toInt() } ?: 0

    fun isCrafting(): Boolean = parent.progress > 0
    protected fun isEnabled(): Boolean = parent.blockState.getValue(BlockStateProperties.ENABLED)

    inner class ResultSlot(id: Int, x: Int, y: Int) : Slot(parent.craftingManager, id, x, y) {
        override fun mayPlace(pStack: ItemStack): Boolean = false
    }

    init {
//        repeat(9) { addSlot(Slot(inventory, it, 8 + it * 18, hotBarY)) }
//        repeat(3) { y -> repeat(9) { x -> addSlot(Slot(inventory, x + y * 9 + 9, 8 + x * 18, inventoryY + y * 18)) } }
        addInventorySlots(inventory, 8, hotBarY, inventoryY)
    }

    final override fun stillValid(pPlayer: Player): Boolean = stillValid(
        ContainerLevelAccess.create(pPlayer.level(), parent.blockPos),
        pPlayer,
        parent.blockState.block
    )
}