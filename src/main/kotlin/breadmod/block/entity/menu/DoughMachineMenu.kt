package breadmod.block.entity.menu

import breadmod.block.entity.DoughMachineBlockEntity
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.block.ModBlocks
import breadmod.registry.screen.ModMenuTypes
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerLevelAccess
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.IFluidHandler
import kotlin.jvm.optionals.getOrNull

class DoughMachineMenu(
    pContainerId: Int,
    private val inventory: Inventory,
    val parent: DoughMachineBlockEntity
) : AbstractContainerMenu(
    ModMenuTypes.DOUGH_MACHINE.get(),
    pContainerId
) {
    constructor(pContainerId: Int, inventory: Inventory, byteBuf: FriendlyByteBuf) : this(
        pContainerId, inventory,
        inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntities.DOUGH_MACHINE.get()).get()
    )

    fun getScaledProgress(): Int = ((parent.progress.toFloat() / parent.maxProgress) * 24).toInt()
    fun getEnergyStoredScaled(): Int = parent.energyHandlerOptional.resolve().getOrNull()?.let { ((it.energyStored.toFloat() / it.maxEnergyStored) * 47).toInt() } ?: 0
    fun isCrafting(): Boolean = parent.progress > 0

    class DoughMachineResultSlot(parent: DoughMachineBlockEntity) : Slot(parent,1, 78, 35) {
        override fun mayPlace(stack: ItemStack): Boolean = false }
    class DoughMachineBucketSlot(parent: DoughMachineBlockEntity) : Slot(parent, 2, 153, 7) {
        override fun mayPlace(stack: ItemStack): Boolean =
            stack.item.let { it is BucketItem && it.fluid.`is`(FluidTags.WATER) } ||
            FluidUtil.getFluidHandler(stack).resolve().getOrNull().let { it?.drain(1, IFluidHandler.FluidAction.SIMULATE)?.let { drained -> drained.amount == 1 && drained.fluid.`is`(FluidTags.WATER) } == true }
    }

    init {
        addSlot(Slot(parent, 0, 26, 34))
        addSlot(DoughMachineResultSlot(parent))
        addSlot(DoughMachineBucketSlot(parent))

        repeat(9) { addSlot(Slot(inventory, it, 8 + it * 18, 142)) }
        repeat(3) { y -> repeat(9) { x -> addSlot(Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18)) } }
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

    override fun stillValid(pPlayer: Player): Boolean = stillValid(
            ContainerLevelAccess.create(pPlayer.level(), parent.blockPos),
            pPlayer,
            ModBlocks.DOUGH_MACHINE_BLOCK.get().block
        )

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
        private const val TE_INVENTORY_SLOT_COUNT = 3 // must be the number of slots you have!
    }
}