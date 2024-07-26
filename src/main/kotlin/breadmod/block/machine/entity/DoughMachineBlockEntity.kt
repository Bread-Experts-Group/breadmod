package breadmod.block.machine.entity

import breadmod.ModMain.modTranslatable
import breadmod.block.machine.entity.menu.DoughMachineMenu
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.FluidContainer
import breadmod.util.capability.FluidContainer.Companion.drain
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.MutableComponent
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.templates.FluidTank

class DoughMachineBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState,
) : AbstractMachineBlockEntity.Progressive.Powered<DoughMachineBlockEntity, DoughMachineRecipe>(
    ModBlockEntityTypes.DOUGH_MACHINE.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.DOUGH_MACHINE,
    IndexableItemHandler(
        listOf(
            64 to StorageDirection.STORE_ONLY,
            64 to StorageDirection.EMPTY_ONLY,
            1 to StorageDirection.BIDIRECTIONAL
        )
    ) to mutableListOf(null, Direction.WEST, Direction.EAST, Direction.DOWN),
    listOf(0),
    1 to 1,
    EnergyBattery(50000, 2000) to mutableListOf(null, Direction.NORTH),
    ForgeCapabilities.FLUID_HANDLER to (FluidContainer(mutableMapOf(
        FluidTank(INPUT_TANK_CAPACITY) to StorageDirection.STORE_ONLY,
        FluidTank(OUTPUT_TANK_CAPACITY) to StorageDirection.EMPTY_ONLY
    )
    ) to mutableListOf(null, Direction.UP))
), MenuProvider {
    companion object {
        const val INPUT_TANK_CAPACITY = 8000
        const val OUTPUT_TANK_CAPACITY = 4000
    }

    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)
    private fun getFluidHandler() = capabilityHolder.capabilityOrNull<FluidContainer>(ForgeCapabilities.FLUID_HANDLER)

    init {
        getItemHandler()?.let {
            it.insertItemCheck = { slot, stack, _ -> slot == 0 || (slot == 2 && stack.item is BucketItem) }
            it.extractItemCheck = { slot, _, _ -> slot == 1 || (slot == 2 && it[slot].item == Items.BUCKET) }
        }
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, p2: Player): AbstractContainerMenu {
        return DoughMachineMenu(pContainerId, pInventory, this)
    }

    override fun getDisplayName(): MutableComponent = modTranslatable("block", "dough_machine")

    override fun consumeRecipe(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<DoughMachineBlockEntity, DoughMachineRecipe>,
        recipe: DoughMachineRecipe
    ): Boolean {
        val fluidHandle = getFluidHandler() ?: return false
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
        val fluidHandle = getFluidHandler() ?: return false
        val itemHandle = getItemHandler() ?: return false
        val outputTank = fluidHandle.allTanks[1]
        return if(recipe.canFitResults(itemHandle to listOf(1), outputTank)) {
            val assembled = recipe.assembleOutputs(craftingManager, pLevel)
            assembled.first.forEach { stack -> itemHandle[1].let { slot -> if(slot.isEmpty) itemHandle[1] = stack.copy() else slot.grow(stack.count) } }
            assembled.second.forEach { stack ->  outputTank.fill(stack, IFluidHandler.FluidAction.EXECUTE) }
            true
        } else false
    }
}