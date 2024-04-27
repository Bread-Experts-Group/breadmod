package breadmod.block.entity.menu

import breadmod.block.entity.DoughMachineBlockEntity
import breadmod.registry.block.ModBlocks
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.SlotItemHandler

@Suppress("SpellCheckingInspection")
class DoughMachineMenu(pContainerId: Int, inv: Inventory, entity: BlockEntity?, data: ContainerData) : AbstractContainerMenu(
    ModMenuTypes.DOUGH_MACHINE.get(), pContainerId) {
    private val blockEntity: DoughMachineBlockEntity
    private val level: Level
    private val data: ContainerData

    constructor(pContainerId: Int, inv: Inventory, extraData: FriendlyByteBuf) : this(
        pContainerId,
        inv,
        inv.player.level().getBlockEntity(extraData.readBlockPos()),
        SimpleContainerData(2)
    )

    fun getScaledProgress(): Int {
        val progress = this.data.get(0)
        val maxProgress = this.data.get(1)
        val progressArrowSize = 24

        return if (maxProgress != 0 && progress != 0) progress * progressArrowSize / maxProgress else 0
    }

    fun isCrafting(): Boolean {
        return data[0] > 0
    }

    init {
        checkContainerSize(inv, 2)
        blockEntity = (entity as DoughMachineBlockEntity)
        this.level = inv.player.level()
        this.data = data

        addPlayerInventory(inv)
        addPlayerHotBar(inv)

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent { iItemHandler: IItemHandler? ->
            this.addSlot(SlotItemHandler(iItemHandler, 0, 56, 17))
            this.addSlot(DoughMachineResultSlot(iItemHandler, 1, 116, 35))
        }

        addDataSlots(data)
    }

    override fun quickMoveStack(playerIn: Player, pIndex: Int): ItemStack {
        val sourceSlot = slots[pIndex]
        if (!sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM

        val sourceStack = sourceSlot.item
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(
                    sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
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

    override fun stillValid(pPlayer: Player): Boolean {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.blockPos), pPlayer, ModBlocks.DOUGH_MACHINE_BLOCK.get().block)
    }

    private fun addPlayerInventory(playerInventory: Inventory) {
        for (i in 0..2) {
            for (l in 0..8) {
                this.addSlot(Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18))
            }
        }
    }

    private fun addPlayerHotBar(playerInventory: Inventory) {
        for (i in 0..8) {
            this.addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

    companion object {
        // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
        // must assign a slot number to each of the slots used by the GUI.
        // For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
        // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
        //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
        //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
        //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
        private const val HOTBAR_SLOT_COUNT = 9
        private const val PLAYER_INVENTORY_ROW_COUNT = 3
        private const val PLAYER_INVENTORY_COLUMN_COUNT = 9
        private const val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
        private const val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT
        private const val VANILLA_FIRST_SLOT_INDEX = 0
        private const val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT

        // THIS YOU HAVE TO DEFINE!
        private const val TE_INVENTORY_SLOT_COUNT = 2 // must be the number of slots you have!
    }
}