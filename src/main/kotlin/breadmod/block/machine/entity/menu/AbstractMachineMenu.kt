package breadmod.block.machine.entity.menu

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.util.capability.EnergyBattery
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities
import kotlin.jvm.optionals.getOrNull

abstract class AbstractMachineMenu<T : AbstractMachineBlockEntity.Progressive<T,R>, R : FluidEnergyRecipe>(
    pMenuType: MenuType<*>,
    pContainerId: Int,
    protected val inventory: Inventory,
    val parent: T,
    hotBarY: Int,
    inventoryY: Int
) : AbstractContainerMenu(pMenuType, pContainerId) {
    open fun getScaledProgress(): Int = parent.currentRecipe.getOrNull()?.let { ((parent.progress.toFloat() / it.time) * 14).toInt() } ?: 0
    open fun getEnergyStoredScaled(): Int = parent.capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.let { ((it.energyStored.toFloat() / it.maxEnergyStored) * 47).toInt() } ?: 0
    fun isCrafting(): Boolean = parent.progress > 0
    protected fun isEnabled(): Boolean = parent.blockState.getValue(BlockStateProperties.ENABLED)

    inner class ResultSlot(id: Int, x: Int, y: Int) : Slot(parent, id, x, y) { override fun mayPlace(pStack: ItemStack): Boolean = false }
    
    init {
        repeat(9) { addSlot(Slot(inventory, it, 8 + it * 18, hotBarY)) }
        repeat(3) { y -> repeat(9) { x -> addSlot(Slot(inventory, x + y * 9 + 9, 8 + x * 18, inventoryY + y * 18)) } }
    }

    // todo investigate "Invalid slotIndex:37" when trying to shift click items from the output slot on machines
    // todo side note: quickMoveStack shouldn't be final since TE_INVENTORY_SLOT_COUNT wouldn't be the same for every machine
    final override fun quickMoveStack(playerIn: Player, pIndex: Int): ItemStack {
        val sourceSlot = slots[pIndex]
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM

        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(
                    sourceStack,
                    TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                            + TE_INVENTORY_SLOT_COUNT, false
                )
            ) {
                return ItemStack.EMPTY // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(
                    sourceStack,
                    VANILLA_FIRST_SLOT_INDEX,
                    VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT,
                    false
                )
            ) {
                return ItemStack.EMPTY
            }
        } else {
            println("Invalid slotIndex:$pIndex")
            return ItemStack.EMPTY
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {
            sourceSlot.set(ItemStack.EMPTY)
        } else {
            sourceSlot.setChanged()
        }
        sourceSlot.onTake(playerIn, sourceStack)
        return copyOfSourceStack
    }

    final override fun stillValid(pPlayer: Player): Boolean = stillValid(
        ContainerLevelAccess.create(pPlayer.level(), parent.blockPos),
        pPlayer,
        parent.blockState.block
    )

    private companion object {
        // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
        // must assign a slot number to each of the slots used by the GUI.
        // For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
        // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
        //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
        //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
        //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
        const val HOTBAR_SLOT_COUNT = 9
        const val PLAYER_INVENTORY_ROW_COUNT = 3
        const val PLAYER_INVENTORY_COLUMN_COUNT = 9
        const val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        const val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        const val VANILLA_FIRST_SLOT_INDEX = 0
        const val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

        // THIS YOU HAVE TO DEFINE!
        const val TE_INVENTORY_SLOT_COUNT = 1 // must be the number of slots you have!
    }
}