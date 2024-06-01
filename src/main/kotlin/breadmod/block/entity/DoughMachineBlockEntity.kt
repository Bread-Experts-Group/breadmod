package breadmod.block.entity

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.block.entity.menu.DoughMachineMenu
import breadmod.network.CapabilityDataPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.CapabilityHolder
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.FluidContainer.Companion.drain
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
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
import kotlin.math.min

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : BlockEntity(ModBlockEntities.DOUGH_MACHINE.get(), pPos, pBlockState), MenuProvider, CraftingContainer {
    companion object {
        const val INPUT_TANK_CAPACITY = 8000
        const val OUTPUT_TANK_CAPACITY = 4000
    }

    override fun setChanged() = super.setChanged().also {
        if(level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityDataPacket(blockPos, updateTag)
        )
    }

    val capabilities = CapabilityHolder(mapOf(
        ForgeCapabilities.ENERGY to (object : EnergyStorage(50000, 2000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(maxReceive, simulate).also { setChanged() }
            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, simulate).also { setChanged() }
        } to null),
        ForgeCapabilities.FLUID_HANDLER to (FluidContainer(mutableMapOf(
            FluidTank(INPUT_TANK_CAPACITY) to StorageDirection.STORE_ONLY,
            FluidTank(OUTPUT_TANK_CAPACITY) to StorageDirection.EMPTY_ONLY
        ), ::setChanged) to null),
        ForgeCapabilities.ITEM_HANDLER to (IndexableItemHandler(3, ::setChanged) to null)
    ))

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
        capabilities.capabilitySided(cap, this.blockState.getValue(HorizontalDirectionalBlock.FACING), side) ?: super.getCapability(cap, side)

    override fun invalidateCaps() {
        capabilities.invalidate()
        super.invalidateCaps()
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, p2: Player): AbstractContainerMenu {
        return DoughMachineMenu(pContainerId, pInventory, this)
    }

    private fun getItemHandler() = capabilities.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    private val recipeDial: RecipeManager.CachedCheck<CraftingContainer, FluidEnergyRecipe> =
        RecipeManager.createCheck(ModRecipeTypes.ENERGY_FLUID_ITEM)
    private var currentRecipe: FluidEnergyRecipe? = null
    private var energyDivision: Int? = null

    var progress = 0; var maxProgress = 0
    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            capabilities.serialize(pTag)
            dataTag.putInt("progress", progress); dataTag.putInt("maxProgress", maxProgress)
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        pTag.getCompound(ModMain.ID).also { tag ->
            capabilities.deserialize(pTag)
            progress = tag.getInt("progress"); maxProgress = tag.getInt("maxProgress")
        }
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: DoughMachineBlockEntity) {
        val energyHandle = capabilities.capabilityOrNull<EnergyStorage>(ForgeCapabilities.ENERGY) ?: return
        val fluidHandle = capabilities.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER) ?: return
        val itemHandle = getItemHandler() ?: return

        itemHandle[2].let {
            if(!it.isEmpty) {
                val item = it.item
                if(item is BucketItem && fluidHandle.space(item.fluid) >= FluidType.BUCKET_VOLUME) {
                    fluidHandle.fill(FluidStack(item.fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE)
                    itemHandle[2] = Items.BUCKET.defaultInstance
                } else {
                    FluidUtil.getFluidHandler(it).ifPresent { stackFluidHandle ->
                        val spaceOfDrained = fluidHandle.space(stackFluidHandle.drain(1, IFluidHandler.FluidAction.SIMULATE).fluid)
                        if(spaceOfDrained >= 50) {
                            fluidHandle.fill(
                                stackFluidHandle.drain(min(50, spaceOfDrained), IFluidHandler.FluidAction.EXECUTE),
                                IFluidHandler.FluidAction.EXECUTE
                            )
                        }
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
                    if(it.canFitResults(itemHandle to listOf(1), outputTank)) {
                        val assembled = it.assembleOutputs(this, pLevel)
                        assembled.first.forEach { stack -> itemHandle[1].let { slot -> if(slot.isEmpty) itemHandle[1] = stack.copy() else slot.grow(stack.count) } }
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
                    recipe.fluidsRequiredTagged?.forEach { (tag, amount) -> inputTank.drain(tag, amount, IFluidHandler.FluidAction.EXECUTE) }
                    recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
                    recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
                    energyDivision = recipe.energy?.let { rf -> (rf.toFloat() / recipe.time).toInt() }
                    currentRecipe = recipe
                }
            }
        }
    }

    override fun clearContent() { getItemHandler()?.clear() }
    override fun getContainerSize(): Int = getItemHandler()?.size ?: 0
    override fun isEmpty(): Boolean = getItemHandler()?.isEmpty() ?: true
    override fun getItem(pSlot: Int): ItemStack = getItemHandler()?.get(pSlot) ?: ItemStack.EMPTY
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = getItemHandler()?.extractItem(pSlot, pAmount, false) ?: ItemStack.EMPTY
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = getItemHandler()?.removeAt(pSlot) ?: ItemStack.EMPTY
    override fun setItem(pSlot: Int, pStack: ItemStack) { getItemHandler()?.set(pSlot, pStack) }
    override fun stillValid(pPlayer: Player): Boolean = getItemHandler() != null
    override fun fillStackedContents(pContents: StackedContents) { TODO("Not yet implemented") }

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1

    override fun getItems(): MutableList<ItemStack> = getItemHandler()?.items ?: mutableListOf()
}