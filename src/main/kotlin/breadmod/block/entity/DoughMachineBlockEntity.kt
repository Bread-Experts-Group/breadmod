package breadmod.block.entity

import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.item.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Containers
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pBlockState), MenuProvider {
    private val itemHandler = ItemStackHandler(2)
    private val inputSlot = 0
    private val outputSlot = 1

    private var lazyItemHandler: LazyOptional<IItemHandler> = LazyOptional.empty()

    var data: ContainerData
    private var progress = 0
    private var maxProgress = 40

    init {
         this.data = object : ContainerData {
            override fun get(pIndex: Int): Int {
                return when (pIndex) {
                    0 -> { this@DoughMachineBlockEntity.progress }
                    1 -> { this@DoughMachineBlockEntity.maxProgress }
                    else -> { 0 }
                }
            }
            override fun set(pIndex: Int, pValue: Int) {
                when (pIndex) {
                    0 -> this@DoughMachineBlockEntity.progress = pValue
                    1 -> this@DoughMachineBlockEntity.maxProgress = pValue
                }
            }
            override fun getCount(): Int { return 2 }
        }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        if(cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast()
        }

        return super.getCapability(cap, side)
    }

    override fun onLoad() {
        super.onLoad()
        lazyItemHandler = LazyOptional.of { itemHandler }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        lazyItemHandler.invalidate()
    }

    fun drops() {
        val inventory = SimpleContainer(itemHandler.slots)
        for (i in 0 until itemHandler.slots) {
            inventory.setItem(i, itemHandler.getStackInSlot(i))
        }
        this.level?.let { Containers.dropContents(it, this.worldPosition, inventory) }
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, p2: Player): AbstractContainerMenu {
        return DoughMachineMenu(pContainerId, pInventory, this, this.data)
    }

    override fun saveAdditional(pTag: CompoundTag) {
        pTag.put("inventory", itemHandler.serializeNBT())
        pTag.putInt("dough_machine.progress", progress)

        super.saveAdditional(pTag)
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        itemHandler.deserializeNBT(pTag.getCompound("inventory"))
        progress = pTag.getInt("dough_machine.progress")
    }

    override fun getDisplayName(): Component {
        return Component.translatable("block.breadmod.dough_machine")
    }

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState) {
        if(hasRecipe()) {
            progress++
            setChanged(pLevel, pPos, pState)
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))

            if(hasProgressFinished()) {
                craftItem()
                progress = 0
            }
        } else {
            progress = 0
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
        }
    }

    private fun craftItem() {
        val result = ItemStack(ModItems.DOUGH.get(), 1)
        this.itemHandler.extractItem(inputSlot, 1, false)

        this.itemHandler.setStackInSlot(outputSlot, ItemStack(result.item, this.itemHandler.getStackInSlot(outputSlot).count + result.count))
    }

    private fun hasRecipe(): Boolean {
        val hasCraftingItem = this.itemHandler.getStackInSlot(inputSlot).item == ModItems.FLOUR.get()
        val result = ItemStack(ModItems.DOUGH.get())

        return hasCraftingItem && canInsertAmountIntoOutputSlot(result.count) && canInsertItemIntoOutputSlot(result.item)
    }

    private fun canInsertItemIntoOutputSlot(item: Item): Boolean {
        return this.itemHandler.getStackInSlot(outputSlot).isEmpty || this.itemHandler.getStackInSlot(outputSlot).`is`(item)
    }

    private fun canInsertAmountIntoOutputSlot(count: Int): Boolean {
        return this.itemHandler.getStackInSlot(outputSlot).count + count <= this.itemHandler.getStackInSlot(outputSlot).maxStackSize
    }

    private fun hasProgressFinished(): Boolean {
        return progress >= maxProgress
    }

}