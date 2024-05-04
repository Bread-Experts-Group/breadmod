package breadmod.block.entity

import breadmod.BreadMod
import breadmod.BreadMod.modTranslatable
import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.item.ModItems
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pBlockState), MenuProvider, WorldlyContainer {
    val data = SimpleContainerData(7)
    init { data[1] = MAX_PROGRESS }

    val items = MutableList(3) { ItemStack.EMPTY }
    fun Collection<ItemStack>.serialize(tag: CompoundTag): CompoundTag {
        this.forEachIndexed { index, stack -> tag.put(index.toString(), stack.serializeNBT()) }
        return tag
    }
    fun Collection<ItemStack>.serialize() = this.serialize(CompoundTag())
    fun MutableList<ItemStack>.deserialize(tag: CompoundTag) = tag.allKeys.forEach { this[it.toInt()] = ItemStack.of(tag.getCompound(it)) }

    private val energyHandlerOptional: LazyOptional<EnergyStorage> = LazyOptional.of {
        object : EnergyStorage(50000, 2000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(maxReceive, simulate).also {
                setChanged()
                data[2] = energyStored
            }

            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, simulate).also {
                setChanged()
                data[2] = energyStored
            }
        }.also { data[3] = it.maxEnergyStored }
    }

    private var lastFluidCount = 0
    val fluidHandlerOptional: LazyOptional<FluidTank> = LazyOptional.of {
        object : FluidTank(50000) {
            override fun onContentsChanged() {
                setChanged()
                data[4] = fluidAmount
            }

            override fun setCapacity(capacity: Int): FluidTank = super.setCapacity(capacity).also {
                setChanged()
                data[5] = capacity
            }

            override fun isFluidValid(stack: FluidStack): Boolean = stack.fluid == Fluids.WATER
        }.also { data[5] = it.capacity }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val currentDirection = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
        return when {
            (cap == ForgeCapabilities.FLUID_HANDLER) && (side == null || side == Direction.UP) -> fluidHandlerOptional.cast()
            (cap == ForgeCapabilities.ENERGY) && (side == null || side == currentDirection.opposite) -> energyHandlerOptional.cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        energyHandlerOptional.invalidate()
        fluidHandlerOptional.invalidate()
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, p2: Player): AbstractContainerMenu {
        return DoughMachineMenu(pContainerId, pInventory, this)
    }

    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(BreadMod.ID, CompoundTag().also { dataTag ->
            dataTag.put("items", items.serialize())
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
            fluidHandlerOptional.ifPresent { dataTag.put("fluids", CompoundTag().also { tag -> it.writeToNBT(tag) }) }
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(BreadMod.ID)
        items.deserialize(dataTag.getCompound("items"))
        energyHandlerOptional.ifPresent {
            it.deserializeNBT(dataTag.get("energy"))
            data[2] = it.energyStored; data[3] = it.maxEnergyStored
        }
        fluidHandlerOptional.ifPresent {
            it.readFromNBT(dataTag.getCompound("fluids"))
            data[4] = it.fluidAmount; data[5] = it.capacity
        }
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: DoughMachineBlockEntity) {
        val energyHandle = pBlockEntity.energyHandlerOptional.resolve().getOrNull() ?: return
        val fluidHandle = pBlockEntity.fluidHandlerOptional.resolve().getOrNull() ?: return

        data[6] = fluidHandle.fluidAmount.compareTo(lastFluidCount)
        lastFluidCount = fluidHandle.fluidAmount

        items[2].let {
            if(!it.isEmpty) {
                if(fluidHandle.space > 1000 && (it.item as? BucketItem)?.fluid?.isSame(Fluids.WATER) == true) {
                    fluidHandle.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                    items[2] = Items.BUCKET.defaultInstance
                } else {
                    FluidUtil.getFluidHandler(it).ifPresent { stackFluidHandle ->
                        fluidHandle.fill(
                            stackFluidHandle.drain(FluidStack(Fluids.WATER, min(50, fluidHandle.space)), IFluidHandler.FluidAction.EXECUTE),
                            IFluidHandler.FluidAction.EXECUTE
                        )
                    }
                }
            }
        }

        if(
            energyHandle.energyStored >= 50 && fluidHandle.fluidAmount >= 25 &&
            items[0].`is`(ModItems.FLOUR.get()) &&
            items[1].let { it.count < it.maxStackSize }
        ) {
            energyHandle.extractEnergy(50, false)
            fluidHandle.drain(25, IFluidHandler.FluidAction.EXECUTE)
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))

            data[0]++
            if(data[0] >= data[1]) {
                items[0].shrink(1)
                if(items[1].`is`(ModItems.DOUGH.get())) items[1].grow(1) else items[1] = ModItems.DOUGH.get().defaultInstance
                data[0] = 0
                setChanged()
            }
        } else {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
            data[0] = 0
        }
    }

    companion object {
        const val MAX_PROGRESS = 20 * 5
    }

    override fun clearContent() = items.forEach { it.count = 0 }
    override fun getContainerSize(): Int = items.size
    override fun isEmpty(): Boolean = items.any { !it.isEmpty }
    override fun getItem(pSlot: Int): ItemStack = items[pSlot]
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = items[pSlot].split(pAmount)
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = items[pSlot].copyAndClear()
    override fun setItem(pSlot: Int, pStack: ItemStack) { items[pSlot] = pStack }
    override fun stillValid(pPlayer: Player): Boolean = true

    //  W  Water (Top)
    // FED Flour (Left), Energy (Back), Dough (Right)
    //  -
    override fun getSlotsForFace(pSide: Direction): IntArray = when(pSide) {
        Direction.WEST -> intArrayOf(0)
        Direction.EAST -> intArrayOf(2)
        else -> intArrayOf()
    }
    // Bad impl? TODO
    override fun canPlaceItemThroughFace(pIndex: Int, pItemStack: ItemStack, pDirection: Direction?): Boolean =
        if(pDirection != null) getSlotsForFace(pDirection).contains(pIndex) else true
    override fun canTakeItemThroughFace(pIndex: Int, pStack: ItemStack, pDirection: Direction): Boolean =
        getSlotsForFace(pDirection).contains(pIndex)
}