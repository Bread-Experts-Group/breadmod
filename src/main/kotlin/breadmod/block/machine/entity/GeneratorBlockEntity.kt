package breadmod.block.machine.entity

import breadmod.recipe.fluidEnergy.generators.GeneratorRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState

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

    override fun clearContent() {
        TODO("Not yet implemented")
    }

    override fun getContainerSize(): Int {
        TODO("Not yet implemented")
    }

    override fun isEmpty(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getItem(pSlot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun removeItemNoUpdate(pSlot: Int): ItemStack {
        TODO("Not yet implemented")
    }

    override fun setItem(pSlot: Int, pStack: ItemStack) {
        TODO("Not yet implemented")
    }

    override fun stillValid(pPlayer: Player): Boolean {
        TODO("Not yet implemented")
    }

    override fun fillStackedContents(pContents: StackedContents) {
        TODO("Not yet implemented")
    }

    override fun getWidth(): Int {
        TODO("Not yet implemented")
    }

    override fun getHeight(): Int {
        TODO("Not yet implemented")
    }

    override fun getItems(): MutableList<ItemStack> {
        TODO("Not yet implemented")
    }
}