package breadmod.block.machine.entity

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.block.machine.entity.menu.WheatCrusherMenu
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.EnergyBattery
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraftforge.common.capabilities.ForgeCapabilities

class WheatCrusherBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : AbstractMachineBlockEntity.Progressive.Powered<WheatCrusherBlockEntity, WheatCrushingRecipe>(
    ModBlockEntities.WHEAT_CRUSHER.get(),
    pPos,
    pBlockState,
    ModRecipeTypes.WHEAT_CRUSHING,
    EnergyBattery(50000, 2000) to null,
    ForgeCapabilities.ITEM_HANDLER to (IndexableItemHandler(listOf(
        64 to StorageDirection.STORE_ONLY,
        64 to StorageDirection.EMPTY_ONLY
    )) to null)
), MenuProvider, CraftingContainer {
    override fun adjustSaveAdditionalProgressive(pTag: CompoundTag) {
        super.adjustSaveAdditionalProgressive(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)?.let {
                dataTag.put("inventory", it.serializeNBT()) }
            capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.let {
                dataTag.put("energy", it.serializeNBT()) }
        })
    }

    override fun adjustLoadProgressive(pTag: CompoundTag) {
        super.adjustLoadProgressive(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)?.deserializeNBT(dataTag.getCompound("inventory"))
        capabilityHolder.capabilityOrNull<EnergyBattery>(ForgeCapabilities.ENERGY)?.deserializeNBT(dataTag.getCompound("energy"))
    }

    override fun recipeTick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: Progressive<WheatCrusherBlockEntity, WheatCrushingRecipe>,
        recipe: WheatCrushingRecipe
    ) {
//        println("serverside recipe progress: ${progress}t / ${progress / 20}s")
//        println("serverside recipe time: ${currentRecipe.getOrNull()?.time}t / ${currentRecipe.getOrNull()?.time?.div(20)}s")
    }


    override fun createMenu(pContainerId: Int, pInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        WheatCrusherMenu(pContainerId, pInventory, this)

    private fun getItemHandler() = capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }

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
            val assembled = recipe.assembleOutputs(this, pLevel)
            assembled.first.forEach { stack -> itemHandle[1].let { slot -> if(slot.isEmpty) itemHandle[1] = stack.copy() else slot.grow(stack.count) } }
            true
        } else false
    }

    override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
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