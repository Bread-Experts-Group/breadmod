package breadmodadvanced.block.entity

import breadmod.block.machine.entity.AbstractMachineBlockEntity
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import breadmodadvanced.recipe.fluidEnergy.generators.DieselGeneratorRecipe
import breadmodadvanced.registry.block.ModBlockEntitiesAdv
import breadmodadvanced.registry.recipe.ModRecipeTypesAdv
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.templates.FluidTank

class DieselGeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractMachineBlockEntity.Progressive.Powered<DieselGeneratorBlockEntity, DieselGeneratorRecipe>(
    ModBlockEntitiesAdv.DIESEL_GENERATOR.get(),
    pPos,
    pBlockState,
    ModRecipeTypesAdv.DIESEL_GENERATOR,
    EnergyBattery(50000, 0, 2000) to null,
    ForgeCapabilities.FLUID_HANDLER to (FluidContainer(mutableMapOf(
        FluidTank(8000) to StorageDirection.STORE_ONLY
    )) to mutableListOf(Direction.UP, null))
) {
    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    override fun clearContent() { getItemHandler()?.clear() }
    override fun getContainerSize(): Int = getItemHandler()?.size ?: 0
    override fun isEmpty(): Boolean = getItemHandler()?.isEmpty() ?: true
    override fun getItem(pSlot: Int): ItemStack = getItemHandler()?.get(pSlot) ?: ItemStack.EMPTY
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = getItemHandler()?.extractItem(pSlot, pAmount, false) ?: ItemStack.EMPTY
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = getItemHandler()?.removeAt(pSlot) ?: ItemStack.EMPTY
    override fun setItem(pSlot: Int, pStack: ItemStack) { getItemHandler()?.set(pSlot, pStack) }
    override fun stillValid(pPlayer: Player): Boolean = getItemHandler() != null
    override fun fillStackedContents(pContents: StackedContents) { getItemHandler()?.get(0)?.let { pContents.accountStack(it) } }
    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = getItemHandler() ?: mutableListOf()
}