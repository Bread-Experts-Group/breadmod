package breadmod.block.machine.entity

import breadmod.recipe.fluidEnergy.generators.GeneratorRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.IndexableItemHandler
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities

class GeneratorBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity.Progressive.Powered<GeneratorBlockEntity, GeneratorRecipe>(
    ModBlockEntities.GENERATOR.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.GENERATOR,
    EnergyBattery(50000, 2000) to null
) {
    /*override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        CoalGeneratorMenu(pContainerId, pPlayerInventory, this)
    override fun getDisplayName(): Component = modTranslatable("block", "generator")*/

    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)
    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }

//    override fun consumeRecipe(
//        pLevel: Level,
//        pPos: BlockPos,
//        pState: BlockState,
//        pBlockEntity: Progressive<GeneratorBlockEntity, GeneratorRecipe>,
//        recipe: GeneratorRecipe
//    ): Boolean {
//        val itemHandle = getItemHandler() ?: return false
//        recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
//        recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
//        return true
//    } // what am I even doing with this anymore.

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