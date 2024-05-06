package breadmod.block.entity

import breadmod.BreadMod
import breadmod.BreadMod.modTranslatable
import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.FluidContainer
import breadmod.util.deserialize
import breadmod.util.serialize
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeManager
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
    pBlockState: BlockState,
) : BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pBlockState), MenuProvider, WorldlyContainer, CraftingContainer {
    val data = SimpleContainerData(7)

    private val storedItems = MutableList(3) { ItemStack.EMPTY }
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
    val fluidHandlerOptional: LazyOptional<FluidContainer> = LazyOptional.of {
        object : FluidContainer(1, { FluidTank(8000) }) {
            override fun contentsChanged() {
                setChanged()
                data[4] = this.amount(Fluids.WATER)
            }

            //override fun isFluidValid(stack: FluidStack): Boolean = stack.fluid == Fluids.WATER
        }.also { data[5] = it.capacity(Fluids.WATER) }
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
            dataTag.put("items", storedItems.serialize())
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
            fluidHandlerOptional.ifPresent { dataTag.put("fluids", it.serializeNBT()) }
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(BreadMod.ID)
        storedItems.deserialize(dataTag.getCompound("items"))
        energyHandlerOptional.ifPresent {
            it.deserializeNBT(dataTag.get("energy"))
            data[2] = it.energyStored; data[3] = it.maxEnergyStored
        }
        fluidHandlerOptional.ifPresent {
            it.deserializeNBT(dataTag.getCompound("fluids"))
            data[4] = it.amount(Fluids.WATER); data[5] = it.capacity(Fluids.WATER)
        }
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    private val recipeDial: RecipeManager.CachedCheck<CraftingContainer, FluidEnergyRecipe> =
        RecipeManager.createCheck(ModRecipeTypes.ENERGY_FLUID_ITEM)
    private var currentRecipe: FluidEnergyRecipe? = null
    private var energyDivision: Int? = 0

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: DoughMachineBlockEntity) {
        val energyHandle = pBlockEntity.energyHandlerOptional.resolve().getOrNull() ?: return
        val fluidHandle = (pBlockEntity.fluidHandlerOptional.resolve().getOrNull() ?: return)

        storedItems[2].let {
            if(!it.isEmpty) {
                val space = fluidHandle.space(Fluids.WATER)
                if(space > 1000 && (it.item as? BucketItem)?.fluid?.isSame(Fluids.WATER) == true) {
                    fluidHandle.fill(FluidStack(Fluids.WATER, 1000), IFluidHandler.FluidAction.EXECUTE)
                    storedItems[2] = Items.BUCKET.defaultInstance
                } else if(space > 0) {
                    FluidUtil.getFluidHandler(it).ifPresent { stackFluidHandle ->
                        fluidHandle.fill(
                            stackFluidHandle.drain(FluidStack(Fluids.WATER, min(50, space)), IFluidHandler.FluidAction.EXECUTE),
                            IFluidHandler.FluidAction.EXECUTE
                        )
                    }
                }
            }
        }

        val fluidAmount = fluidHandle.amount(Fluids.WATER)
        data[6] = fluidAmount.compareTo(lastFluidCount)
        lastFluidCount = fluidAmount

        currentRecipe.also {
            if(it != null) {
                if(data[0] < data[1]) {
                    data[0]++
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))
                    energyDivision?.let { if(energyHandle.extractEnergy(it, false) != it) data[0]-- }
                } else if(it.canFitResults(storedItems to listOf(1), fluidHandle)) {
                    val assembled = it.assembleOutputs(this, pLevel)
                    assembled.first.forEach { stack -> storedItems[1].let { slot -> if(slot.isEmpty) storedItems[1] = stack.copy() else slot.grow(stack.count) } }
                    assembled.second.forEach { stack -> fluidHandle.fill(stack, IFluidHandler.FluidAction.EXECUTE) }
                    currentRecipe = null
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
                data[0] = 0
                recipeDial.getRecipeFor(pBlockEntity, pLevel).ifPresent { recipe ->
                    data[1] = recipe.time
                    recipe.fluidsRequired?.forEach { fluidHandle.drain(it, IFluidHandler.FluidAction.EXECUTE) }
                    recipe.fluidsRequiredTagged?.forEach { fluidHandle.drain(it.first, it.second, IFluidHandler.FluidAction.EXECUTE) }
                    recipe.itemsRequired?.forEach { storedItems[0].shrink(it.count) }
                    recipe.itemsRequiredTagged?.forEach { storedItems[0].shrink(it.second) }
                    energyDivision = recipe.energy?.let { (it.toFloat() / recipe.time).toInt() }
                    currentRecipe = recipe
                }
            }
        }
    }

    override fun clearContent() = storedItems.forEach { it.count = 0 }
    override fun getContainerSize(): Int = storedItems.size
    override fun isEmpty(): Boolean = storedItems.any { !it.isEmpty }
    override fun getItem(pSlot: Int): ItemStack = storedItems[pSlot]
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = storedItems[pSlot].split(pAmount)
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = storedItems[pSlot].copyAndClear()
    override fun setItem(pSlot: Int, pStack: ItemStack) { storedItems[pSlot] = pStack }
    override fun stillValid(pPlayer: Player): Boolean = true

    override fun fillStackedContents(pContents: StackedContents) { pContents.accountStack(storedItems[0]) }
    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = storedItems

    //  W  Water (Top)
    // FED Flour (Left), Energy (Back), Dough (Right)
    //  -
    override fun getSlotsForFace(pSide: Direction): IntArray = when(pSide) {
        Direction.NORTH -> intArrayOf(0)
        Direction.SOUTH -> intArrayOf(2)
        else -> intArrayOf()
    }
    // Bad impl? TODO
    override fun canPlaceItemThroughFace(pIndex: Int, pItemStack: ItemStack, pDirection: Direction?): Boolean =
        if(pDirection != null) getSlotsForFace(pDirection).contains(pIndex) else true
    override fun canTakeItemThroughFace(pIndex: Int, pStack: ItemStack, pDirection: Direction): Boolean =
        getSlotsForFace(pDirection).contains(pIndex)
}