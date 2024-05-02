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
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import net.minecraftforge.items.ItemStackHandler
import kotlin.jvm.optionals.getOrNull

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pBlockState), MenuProvider {
    val data = SimpleContainerData(6)
    init { data[1] = MAX_PROGRESS }

    val itemHandlerOptional: LazyOptional<ItemStackHandler> = LazyOptional.of {
        object : ItemStackHandler(3) {
            override fun onContentsChanged(slot: Int) = setChanged()
        }
    }
    val energyHandlerOptional: LazyOptional<EnergyStorage> = LazyOptional.of {
        object : EnergyStorage(100000000, 2000) {
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
    val fluidHandlerOptional: LazyOptional<FluidTank> = LazyOptional.of {
        object : FluidTank(5000) {
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
            cap == ForgeCapabilities.ITEM_HANDLER -> itemHandlerOptional.cast()
            (cap == ForgeCapabilities.FLUID_HANDLER) && (side == null || side == Direction.UP) -> fluidHandlerOptional.cast()
            (cap == ForgeCapabilities.ENERGY) && (side == null || side == currentDirection.opposite) -> energyHandlerOptional.cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        itemHandlerOptional.invalidate()
        energyHandlerOptional.invalidate()
        fluidHandlerOptional.invalidate()
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, p2: Player): AbstractContainerMenu {
        return DoughMachineMenu(pContainerId, pInventory, this)
    }

    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(BreadMod.ID, CompoundTag().also { dataTag ->
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
            itemHandlerOptional.ifPresent { dataTag.put("items", it.serializeNBT()) }
            fluidHandlerOptional.ifPresent { dataTag.put("fluids", CompoundTag().also { tag -> it.writeToNBT(tag) }) }
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(BreadMod.ID)
        energyHandlerOptional.ifPresent { it.deserializeNBT(dataTag.get("energy")) }
        itemHandlerOptional.ifPresent { it.deserializeNBT(dataTag.getCompound("items")) }
        fluidHandlerOptional.ifPresent { it.readFromNBT(dataTag.getCompound("fluids")) }
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: DoughMachineBlockEntity) {
        val energyHandle = pBlockEntity.energyHandlerOptional.resolve().getOrNull() ?: return
        val fluidHandle = pBlockEntity.fluidHandlerOptional.resolve().getOrNull() ?: return
        val itemHandle = pBlockEntity.itemHandlerOptional.resolve().getOrNull() ?: return

        itemHandle.getStackInSlot(2).let {
            if(!it.isEmpty && (it.item as? BucketItem)?.fluid?.isSame(Fluids.WATER) == true && fluidHandle.space > 1000) {
                fluidHandle.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                itemHandle.setStackInSlot(2, ItemStack(Items.BUCKET))
            }
        }

        if(
            energyHandle.energyStored >= 50 && fluidHandle.fluidAmount >= 25 &&
            itemHandle.getStackInSlot(0).`is`(ModItems.FLOUR.get()) &&
            itemHandle.getStackInSlot(1).let { it.count < it.maxStackSize }
        ) {
            energyHandle.extractEnergy(50, false)
            fluidHandle.drain(25, IFluidHandler.FluidAction.EXECUTE)
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))

            data[0]++
            if(data[0] >= data[1]) {
                itemHandle.extractItem(0, 1, false)
                itemHandle.insertItem(1, ItemStack(ModItems.DOUGH.get()), false)
                data[0] = 0
            }

            setChanged()
        } else {
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
            data[0] = 0
        }
    }

    companion object {
        const val MAX_PROGRESS = 20 * 5
    }
}