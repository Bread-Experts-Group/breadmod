package breadmod.block.machine.entity

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.block.machine.entity.menu.DoughMachineMenu
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.FluidContainer.Companion.drain
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractMachineBlockEntity.Progressive.Powered<DoughMachineBlockEntity, DoughMachineRecipe>(
    ModBlockEntities.DOUGH_MACHINE.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.DOUGH_MACHINE,
    EnergyBattery(50000, 2000) to null,
    ForgeCapabilities.FLUID_HANDLER to (FluidContainer(mutableMapOf(
        FluidTank(INPUT_TANK_CAPACITY) to StorageDirection.STORE_ONLY,
        FluidTank(OUTPUT_TANK_CAPACITY) to StorageDirection.EMPTY_ONLY
    )) to null),
    ForgeCapabilities.ITEM_HANDLER to (IndexableItemHandler(listOf(
        64 to StorageDirection.STORE_ONLY,
        64 to StorageDirection.EMPTY_ONLY,
        1 to StorageDirection.BIDIRECTIONAL
    )) to null)
), MenuProvider {
    companion object {
        const val INPUT_TANK_CAPACITY = 8000
        const val OUTPUT_TANK_CAPACITY = 4000
    }

    override fun adjustSaveAdditionalProgressive(pTag: CompoundTag) {
        super.adjustSaveAdditionalProgressive(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)?.let {
                dataTag.put("inventory", it.serializeNBT()) }
            capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.let {
                dataTag.put("energy", it.serializeNBT()) }
            capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)?.let {
                dataTag.put("fluid", it.serializeNBT()) }
        })
    }

    override fun adjustLoadProgressive(pTag: CompoundTag) {
        super.adjustLoadProgressive(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)?.deserializeNBT(dataTag.getCompound("inventory"))
        capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.deserializeNBT(dataTag.getCompound("energy"))
        capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)?.deserializeNBT(dataTag.getCompound("fluid"))
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, p2: Player): AbstractContainerMenu {
        return DoughMachineMenu(pContainerId, pInventory, this)
    }

    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    override fun consumeRecipe(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<DoughMachineBlockEntity, DoughMachineRecipe>,
        recipe: DoughMachineRecipe
    ): Boolean {
        val fluidHandle = capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER) ?: return false
        val itemHandle = getItemHandler() ?: return false
        val inputTank = fluidHandle.allTanks[0]
        recipe.fluidsRequired?.forEach { stack -> inputTank.drain(stack, IFluidHandler.FluidAction.EXECUTE) }
        recipe.fluidsRequiredTagged?.forEach { (tag, amount) -> inputTank.drain(tag, amount, IFluidHandler.FluidAction.EXECUTE) }
        recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
        recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
        return true
    }

    override fun recipeDone(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<DoughMachineBlockEntity, DoughMachineRecipe>,
        recipe: DoughMachineRecipe
    ): Boolean {
        val fluidHandle = capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER) ?: return false
        val itemHandle = getItemHandler() ?: return false

        progress = 0
        val outputTank = fluidHandle.allTanks[1]
        return if(recipe.canFitResults(itemHandle to listOf(1), outputTank)) {
            val assembled = recipe.assembleOutputs(this, pLevel)
            assembled.first.forEach { stack -> itemHandle[1].let { slot -> if(slot.isEmpty) itemHandle[1] = stack.copy() else slot.grow(stack.count) } }
            assembled.second.forEach { stack ->  outputTank.fill(stack, IFluidHandler.FluidAction.EXECUTE) }
            true
        } else false
    }

    override fun clearContent() { getItemHandler()?.clear() }
    override fun getContainerSize(): Int = getItemHandler()?.size ?: 0
    override fun isEmpty(): Boolean = getItemHandler()?.isEmpty() ?: true
    override fun getItem(pSlot: Int): ItemStack = getItemHandler()?.get(pSlot) ?: ItemStack.EMPTY
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = getItemHandler()?.get(pSlot)?.split(pAmount) ?: ItemStack.EMPTY
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = getItemHandler()?.get(pSlot)?.copyAndClear() ?: ItemStack.EMPTY
    override fun setItem(pSlot: Int, pStack: ItemStack) { getItemHandler()?.set(pSlot, pStack) }
    override fun stillValid(pPlayer: Player): Boolean = getItemHandler() != null
    override fun fillStackedContents(pContents: StackedContents) { getItemHandler()?.get(0)?.let { pContents.accountStack(it) } }
    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = getItemHandler() ?: mutableListOf()
}