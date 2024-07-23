package breadmod.block.machine.entity

import breadmod.ModMain.modTranslatable
import breadmod.block.machine.entity.menu.WheatCrusherMenu
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.registry.block.ModBlockEntityTypes
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities

class WheatCrusherBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity.Progressive.Powered<WheatCrusherBlockEntity, WheatCrushingRecipe>(
    ModBlockEntityTypes.WHEAT_CRUSHER.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.WHEAT_CRUSHING,
    IndexableItemHandler(listOf(
        64 to StorageDirection.STORE_ONLY,
        64 to StorageDirection.EMPTY_ONLY
    )) to null,
    listOf(0),
    1 to 1,
    EnergyBattery(50000, 2000) to null
), MenuProvider {
    override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")

    override fun createMenu(pContainerId: Int, pInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        WheatCrusherMenu(pContainerId, pInventory, this)

    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    override fun consumeRecipe(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<WheatCrusherBlockEntity, WheatCrushingRecipe>,
        recipe: WheatCrushingRecipe
    ): Boolean {
        val itemHandle = getItemHandler() ?: return false
        recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
        recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
        return true
    }

    override fun recipeDone(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<WheatCrusherBlockEntity, WheatCrushingRecipe>,
        recipe: WheatCrushingRecipe
    ): Boolean {
        val itemHandle = getItemHandler() ?: return false
        return if(recipe.canFitResults(itemHandle to listOf(1), null)) {
            val assembled = recipe.assembleOutputs(cManager, pLevel)
            assembled.first.forEach { stack -> itemHandle[1].let { slot -> if(slot.isEmpty) itemHandle[1] = stack.copy() else slot.grow(stack.count) } }
            true
        } else false
    }
}