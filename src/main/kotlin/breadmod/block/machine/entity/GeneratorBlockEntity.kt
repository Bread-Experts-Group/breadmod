package breadmod.block.machine.entity

import breadmod.recipe.fluidEnergy.generators.GeneratorRecipe
import breadmod.registry.block.ModBlockEntities
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
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.templates.FluidTank
import net.minecraftforge.registries.RegistryObject

class GeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
    pRecipeType: RegistryObject<RecipeType<GeneratorRecipe>>
) : AbstractMachineBlockEntity.Progressive.Powered<GeneratorBlockEntity, GeneratorRecipe>(
    ModBlockEntities.GENERATOR.get(),
    pPos,
    pBlockState,
    pRecipeType,
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

    override fun recipeTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<GeneratorBlockEntity, GeneratorRecipe>,
        recipe: GeneratorRecipe
    ) {
        val sides = (capabilityHolder.capabilities[ForgeCapabilities.ENERGY] ?: return).second
        capabilityHolder.capability<EnergyBattery>(ForgeCapabilities.ENERGY).distribute(
            pLevel, pPos, sides,
            blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)
        )
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