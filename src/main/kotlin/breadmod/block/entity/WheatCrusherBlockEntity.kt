package breadmod.block.entity

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.block.entity.menu.WheatCrusherMenu
import breadmod.network.CapabilityDataPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.capability.CapabilityHolder
import breadmod.util.capability.IndexableItemHandler
import breadmod.util.capability.StorageDirection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
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
import net.minecraftforge.network.PacketDistributor

class WheatCrusherBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntities.WHEAT_CRUSHER.get(), pPos, pBlockState), MenuProvider, CraftingContainer {
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
        ForgeCapabilities.ITEM_HANDLER to (IndexableItemHandler(listOf(
            64 to StorageDirection.STORE_ONLY,
            64 to StorageDirection.EMPTY_ONLY
        ), ::setChanged) to null)
    ))

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> =
        capabilities.capabilitySided(cap, this.blockState.getValue(HorizontalDirectionalBlock.FACING), side) ?: super.getCapability(cap, side)

    override fun invalidateCaps() {
        capabilities.invalidate()
        super.invalidateCaps()
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        WheatCrusherMenu(pContainerId, pInventory, this)

    private fun getItemHandler() = capabilities.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    private val recipeDial: RecipeManager.CachedCheck<CraftingContainer, WheatCrushingRecipe> = RecipeManager.createCheck(ModRecipeTypes.WHEAT_CRUSHING)
    private var currentRecipe: WheatCrushingRecipe? = null
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
        val dataTag = pTag.getCompound(ModMain.ID)
        capabilities.deserialize(pTag)
        progress = dataTag.getInt("progress"); maxProgress = dataTag.getInt("maxProgress")
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: WheatCrusherBlockEntity) {
        val energyHandle = capabilities.capabilityOrNull<EnergyStorage>(ForgeCapabilities.ENERGY) ?: return
        val itemHandle = getItemHandler() ?: return

        currentRecipe.also {
            if(it != null) {
                if(progress < maxProgress) {
                    progress++
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))
                    energyDivision?.let { rfd -> if(energyHandle.extractEnergy(rfd, false) != rfd) progress-- }
                } else {
                    if(it.canFitResults(itemHandle to listOf(1), null)) {
                        val assembled = it.assembleOutputs(this, pLevel)
                        assembled.first.forEach { stack -> itemHandle[1].let { slot -> if(slot.isEmpty) itemHandle[1] = stack.copy() else slot.grow(stack.count) } }
                        setChanged()
                        currentRecipe = null
                        progress = 0
                    }
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
                recipeDial.getRecipeFor(pBlockEntity, pLevel).ifPresent { recipe ->
                    maxProgress = recipe.time
                    recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
                    recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
                    energyDivision = recipe.energy?.let { fe -> (fe.toFloat() / recipe.time).toInt() }
                    currentRecipe = recipe
                }
            }
        }
    }

    override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
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