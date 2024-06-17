package breadmod.block.machine.entity

import breadmod.recipe.fluidEnergy.generators.GeneratorRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.templates.FluidTank
import kotlin.math.min

class GeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity.Progressive.Powered<GeneratorBlockEntity, GeneratorRecipe>(
    ModBlockEntities.GENERATOR.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.GENERATOR,
    EnergyBattery(50000, 0, 2000) to mutableListOf(Direction.WEST, null),
    ForgeCapabilities.FLUID_HANDLER to (FluidContainer(mutableMapOf(FluidTank(8000) to StorageDirection.STORE_ONLY)) to mutableListOf(Direction.UP, null)),
    ForgeCapabilities.ITEM_HANDLER to (IndexableItemHandler(mutableListOf(64 to StorageDirection.STORE_ONLY)) to mutableListOf(Direction.UP, null))
) {
    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)
    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }

    override fun consumeRecipe(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<GeneratorBlockEntity, GeneratorRecipe>,
        recipe: GeneratorRecipe
    ): Boolean {
        val itemHandle = getItemHandler() ?: return false
        recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
        recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
        return true
    }

    private fun runSignsOne(lambda: (Int) -> Unit) {
        var i = -1
        while (i < 2) {
            lambda(i)
            i += 2
        }
    }

    override fun postTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractMachineBlockEntity<GeneratorBlockEntity>
    ) {
        val energyHandle = capabilityHolder.capability<EnergyBattery>(ForgeCapabilities.ENERGY)
        val blockEntities = buildList {
            runSignsOne { x -> pLevel.getBlockEntity(pPos.offset(x, 0, 0))?.getCapability(ForgeCapabilities.ENERGY)?.let { add(it) } }
            runSignsOne { y -> pLevel.getBlockEntity(pPos.offset(0, y, 0))?.getCapability(ForgeCapabilities.ENERGY)?.let { add(it) } }
            runSignsOne { z -> pLevel.getBlockEntity(pPos.offset(0, 0, z))?.getCapability(ForgeCapabilities.ENERGY)?.let { add(it) } }
        }

        if(blockEntities.isNotEmpty()) {
            val total = min(energyHandle.bMaxExtract, energyHandle.stored)
            val toDistribute = total / blockEntities.size
            var distributed = 0
            blockEntities.forEach { opt -> opt.ifPresent {
                distributed += it.receiveEnergy(toDistribute, true)
                if(distributed > total) { energyHandle.extractEnergy(total, false); return@ifPresent }
            } }
            energyHandle.extractEnergy(distributed, false)
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
    override fun fillStackedContents(pContents: StackedContents) { getItemHandler()?.get(0)?.let { pContents.accountStack(it) } }
    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = getItemHandler() ?: mutableListOf()
}