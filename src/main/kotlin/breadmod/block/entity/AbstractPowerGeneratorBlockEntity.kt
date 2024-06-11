package breadmod.block.entity

import breadmod.ModMain
import breadmod.network.CapabilityDataPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
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
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.EnergyStorage
import net.minecraftforge.network.PacketDistributor

abstract class AbstractPowerGeneratorBlockEntity<R : FluidEnergyRecipe>(
    pType: BlockEntityType<*>,
    pPos: BlockPos,
    pBlockState: BlockState,
    pRecipeType: RecipeType<R>
) : BlockEntity(pType, pPos, pBlockState), MenuProvider, CraftingContainer {
    override fun setChanged() {
        if(level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityDataPacket(blockPos, updateTag)
        )
    }

    // todo fix power extracting, possible deadlock while recipe is running
    val capabilities = CapabilityHolder(mapOf(
        ForgeCapabilities.ENERGY to (object : EnergyStorage(100000, 10000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(0, false).also { setChanged() }
            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, false).also { setChanged() }
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

    abstract override fun createMenu(pContainerId: Int, pPlayerInventory: Inventory, pPlayer: Player): AbstractContainerMenu

    private fun getItemHandler() = capabilities.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER)

    private val recipeDial: RecipeManager.CachedCheck<CraftingContainer, R> = RecipeManager.createCheck(pRecipeType)
    private var currentRecipe: R? = null
    private var energyDivision: Int? = null

    var enabled: Boolean = this.blockState.getValue(BlockStateProperties.ENABLED)
    var progress = 0; var maxProgress = 0
    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            capabilities.serialize(pTag)
            dataTag.putInt("progress", progress); dataTag.putInt("maxProgress", maxProgress)
            dataTag.putBoolean("enabled", enabled)
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        pTag.getCompound(ModMain.ID).also { tag ->
            capabilities.deserialize(pTag)
            progress = tag.getInt("progress"); maxProgress = tag.getInt("maxProgress")
            enabled = tag.getBoolean("enabled")
        }
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }
    abstract override fun getDisplayName(): Component

    fun tick(
        pLevel: Level,
        pPos: BlockPos,
        pState: BlockState,
        pBlockEntity: AbstractPowerGeneratorBlockEntity<R>
    ) {
        val energyHandle = capabilities.capabilityOrNull<EnergyStorage>(ForgeCapabilities.ENERGY) ?: return
        val itemHandle = getItemHandler() ?: return

        currentRecipe.also { // todo rework ticking logic to generate power with a positive value in the recipe instead of negative
            if(it != null) {
                if(progress < maxProgress && energyHandle.energyStored < energyHandle.maxEnergyStored && enabled) {
                    progress++
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))
                    energyDivision?.let { rfd -> if(energyHandle.extractEnergy(rfd, false) != rfd) progress-- }
                } else if(progress >= maxProgress) {
                    setChanged()
                    progress = 0
                    currentRecipe = null
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
                recipeDial.getRecipeFor(pBlockEntity, pLevel).ifPresent { recipe ->
                    maxProgress = recipe.time
                    recipe.itemsRequired?.forEach { stack -> itemHandle[0].shrink(stack.count) }
                    recipe.itemsRequiredTagged?.forEach { tag -> itemHandle[0].shrink(tag.second) }
                    energyDivision = recipe.energy?.let { rf -> (rf.toFloat() / recipe.time).toInt() }
                    currentRecipe = recipe
                }
            }
        }
    }

    override fun clearContent() { getItemHandler()?.forEach { it.count = 0 } }
    override fun getContainerSize(): Int = getItemHandler()?.size ?: 0
    override fun isEmpty(): Boolean = getItemHandler()?.any { !it.isEmpty } ?: true
    override fun getItem(pSlot: Int): ItemStack = getItemHandler()?.get(pSlot) ?: ItemStack.EMPTY
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = getItemHandler()?.get(pSlot)?.split(pAmount) ?: ItemStack.EMPTY
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = getItemHandler()?.get(pSlot)?.copyAndClear() ?: ItemStack.EMPTY
    override fun setItem(pSlot: Int, pStack: ItemStack) { getItemHandler()?.set(pSlot, pStack) }
    override fun stillValid(pPlayer: Player): Boolean = true
    override fun fillStackedContents(pContents: StackedContents) { getItemHandler()?.get(0)?.let { pContents.accountStack(it) } }
    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = getItemHandler() ?: mutableListOf()
}