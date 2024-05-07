package breadmod.block.entity

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.network.CapabilityDataPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.FluidContainer
import breadmod.util.FluidContainer.Companion.drain
import breadmod.util.deserialize
import breadmod.util.serialize
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.FluidTags
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank
import net.minecraftforge.network.PacketDistributor
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pBlockState), MenuProvider, WorldlyContainer, CraftingContainer {
    override fun setChanged() = super.setChanged().also {
        if(level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityDataPacket(blockPos, updateTag)
        )
    }

    private val storedItems = MutableList(3) { ItemStack.EMPTY }
    val energyHandlerOptional: LazyOptional<EnergyStorage> = LazyOptional.of {
        object : EnergyStorage(50000, 2000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(maxReceive, simulate).also { setChanged() }
            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, simulate).also { setChanged() }
        }
    }
    val fluidHandlerOptional: LazyOptional<FluidContainer> = LazyOptional.of {
        object : FluidContainer(mutableMapOf(FluidTank(8000) to TankFlow.FILL_ONLY, FluidTank(4000) to TankFlow.DRAIN_ONLY)) {
            override fun contentsChanged() { setChanged() }
        }
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

    private val recipeDial: RecipeManager.CachedCheck<CraftingContainer, FluidEnergyRecipe> =
        RecipeManager.createCheck(ModRecipeTypes.ENERGY_FLUID_ITEM)
    private var currentRecipe: FluidEnergyRecipe? = null
    private var energyDivision: Int? = null

    var progress = 0; var maxProgress = 0
    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            dataTag.put("items", storedItems.serialize())
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
            fluidHandlerOptional.ifPresent { dataTag.put("fluids", it.serializeNBT()) }
            dataTag.putInt("progress", progress); dataTag.putInt("maxProgress", maxProgress)
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        storedItems.deserialize(dataTag.getCompound("items"))
        energyHandlerOptional.ifPresent {
            it.deserializeNBT(dataTag.get("energy"))
        }
        fluidHandlerOptional.ifPresent {
            it.deserializeNBT(dataTag.getCompound("fluids"))
        }
        progress = dataTag.getInt("progress")
        maxProgress = dataTag.getInt("maxProgress")
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: DoughMachineBlockEntity) {
        val energyHandle = pBlockEntity.energyHandlerOptional.resolve().getOrNull() ?: return
        val fluidHandle = (pBlockEntity.fluidHandlerOptional.resolve().getOrNull() ?: return)

        storedItems[2].let {
            if(!it.isEmpty) {
                val item = it.item
                val space = fluidHandle.space(FluidTags.WATER)
                if(space > FluidType.BUCKET_VOLUME && item is BucketItem && item.fluid.`is`(FluidTags.WATER)) {
                    fluidHandle.fill(FluidStack(item.fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE)
                    storedItems[2] = Items.BUCKET.defaultInstance
                } else if(space > 0) {
                    FluidUtil.getFluidHandler(it).ifPresent { stackFluidHandle ->
                        fluidHandle.fill(
                            stackFluidHandle.drain(min(50, space), IFluidHandler.FluidAction.EXECUTE),
                            IFluidHandler.FluidAction.EXECUTE
                        )
                    }
                }
            }
        }

        currentRecipe.also {
            if(it != null) {
                if(progress < maxProgress) {
                    progress++
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))
                    energyDivision?.let { rfd -> if(energyHandle.extractEnergy(rfd, false) != rfd) progress-- }
                } else {
                    val outputTank = fluidHandle.allTanks[1]
                    if(it.canFitResults(storedItems to listOf(1), outputTank)) {
                        val assembled = it.assembleOutputs(this, pLevel)
                        assembled.first.forEach { stack -> storedItems[1].let { slot -> if(slot.isEmpty) storedItems[1] = stack.copy() else slot.grow(stack.count) } }
                        assembled.second.forEach { stack ->  outputTank.fill(stack, IFluidHandler.FluidAction.EXECUTE) }
                        setChanged()
                        currentRecipe = null
                        progress = 0
                    }
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
                recipeDial.getRecipeFor(pBlockEntity, pLevel).ifPresent { recipe ->
                    maxProgress = recipe.time
                    val inputTank = fluidHandle.allTanks[0]
                    recipe.fluidsRequired?.forEach { stack -> inputTank.drain(stack, IFluidHandler.FluidAction.EXECUTE) }
                    recipe.fluidsRequiredTagged?.forEach { tag -> inputTank.drain(tag.first, tag.second, IFluidHandler.FluidAction.EXECUTE) }
                    recipe.itemsRequired?.forEach { stack -> storedItems[0].shrink(stack.count) }
                    recipe.itemsRequiredTagged?.forEach { tag -> storedItems[0].shrink(tag.second) }
                    energyDivision = recipe.energy?.let { rf -> (rf.toFloat() / recipe.time).toInt() }
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