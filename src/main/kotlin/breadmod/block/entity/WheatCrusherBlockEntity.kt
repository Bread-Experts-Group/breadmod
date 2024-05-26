package breadmod.block.entity

import breadmod.ModMain
import breadmod.ModMain.modTranslatable
import breadmod.block.entity.menu.WheatCrusherMenu
import breadmod.network.CapabilityDataPacket
import breadmod.network.PacketHandler.NETWORK
import breadmod.recipe.WheatCrusherRecipe
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.recipe.ModRecipeTypes
import breadmod.util.deserialize
import breadmod.util.serialize
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
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
import kotlin.jvm.optionals.getOrNull

class WheatCrusherBlockEntity(
    pPos: BlockPos,
    pBlockState: BlockState
) : BlockEntity(ModBlockEntities.WHEAT_CRUSHER.get(), pPos, pBlockState), MenuProvider, WorldlyContainer, CraftingContainer {
    override fun setChanged() = super.setChanged().also {
        if(level is ServerLevel) NETWORK.send(
            PacketDistributor.TRACKING_CHUNK.with { (level as ServerLevel).getChunkAt(blockPos) },
            CapabilityDataPacket(blockPos, updateTag)
        )
    }

    private val storedItems = MutableList(3) { ItemStack.EMPTY }

    val energyHandlerOptional: LazyOptional<EnergyStorage> = LazyOptional.of {
        object : EnergyStorage(50000, 2000) {
            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int = super.receiveEnergy(maxReceive, simulate).also { setChanged() }
            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = super.extractEnergy(maxExtract, simulate).also { setChanged() }
        }
    }

    override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
        val currentDirection = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
        return when {
            (cap == ForgeCapabilities.ENERGY) && (side == null || side == currentDirection.opposite) -> energyHandlerOptional.cast()
            else -> super.getCapability(cap, side)
        }
    }

    override fun invalidateCaps() {
        super.invalidateCaps()
        energyHandlerOptional.invalidate()
    }

    override fun createMenu(pContainerId: Int, pInventory: Inventory, pPlayer: Player): AbstractContainerMenu =
        WheatCrusherMenu(pContainerId, pInventory, this)

    private val recipeDial: RecipeManager.CachedCheck<CraftingContainer, WheatCrusherRecipe> =
        RecipeManager.createCheck(ModRecipeTypes.WHEAT_CRUSHING)
    private var currentRecipe: WheatCrusherRecipe? = null
    private var energyDivision: Int? = null

    var progress = 0; var maxProgress = 0
    override fun saveAdditional(pTag: CompoundTag) {
        super.saveAdditional(pTag)
        pTag.put(ModMain.ID, CompoundTag().also { dataTag ->
            dataTag.put("items", storedItems.serialize())
            energyHandlerOptional.ifPresent { dataTag.put("energy", it.serializeNBT()) }
            dataTag.putInt("progress", progress); dataTag.putInt("maxProgress", maxProgress)
        })
    }

    override fun load(pTag: CompoundTag) {
        super.load(pTag)
        val dataTag = pTag.getCompound(ModMain.ID)
        storedItems.deserialize(dataTag.getCompound("items"))
        energyHandlerOptional.ifPresent {
            it.deserializeNBT(dataTag.get("energy"))
        }
        progress = dataTag.getInt("progress")
        maxProgress = dataTag.getInt("maxProgress")
    }

    override fun getUpdateTag(): CompoundTag = super.getUpdateTag().also { saveAdditional(it) }

    fun tick(pLevel: Level, pPos: BlockPos, pState: BlockState, pBlockEntity: WheatCrusherBlockEntity) {
        val energyHandle = pBlockEntity.energyHandlerOptional.resolve().getOrNull() ?: return

        currentRecipe.also {
            if(it != null) {
                if(progress < maxProgress) {
                    progress++
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, true))
                    // I'm going to assume this function here this function subtracts the progress every tick if there's not enough energy present
                    energyDivision?.let { rfd -> if(energyHandle.extractEnergy(rfd, false) != rfd) progress-- }
                } else {
                    if(it.canFitResults(storedItems to listOf(1))) {
                        val assembled = it.assembleOutputs(this, pLevel)
                        assembled.forEach {
                            stack -> storedItems[1].let {
                                slot -> if(slot.isEmpty) storedItems[1] = stack.copy() else
                                    slot.grow(stack.count) } }
                        setChanged()
                        currentRecipe = null
                        progress = 0
                    }
                }
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.LIT, false))
                recipeDial.getRecipeFor(pBlockEntity, pLevel).ifPresent { recipe ->
                    maxProgress = recipe.time
                    recipe.itemsRequired?.forEach { stack -> storedItems[0].shrink(stack.count) } // gregtech lookin ass recipe logic
                    recipe.itemsRequiredTagged?.forEach { tag -> storedItems[0].shrink(tag.second) }
                    energyDivision = recipe.energy?.let { fe -> (fe.toFloat() / recipe.time).toInt() }
                    currentRecipe = recipe
                }
            }
        }
    }

    override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
    override fun clearContent() = storedItems.forEach { it.count = 0 }
    override fun getContainerSize(): Int = storedItems.size
    override fun isEmpty(): Boolean = storedItems.any { !it.isEmpty }
    override fun getItem(pSlot: Int): ItemStack = storedItems[pSlot]
    override fun removeItem(pSlot: Int, pAmount: Int): ItemStack = storedItems[pSlot].split(pAmount)
    override fun removeItemNoUpdate(pSlot: Int): ItemStack = storedItems[pSlot].copyAndClear()
    override fun setItem(pSlot: Int, pStack: ItemStack) { storedItems[pSlot] = pStack }
    override fun stillValid(pPlayer: Player): Boolean = true

    override fun fillStackedContents(pContents: StackedContents) { pContents.accountStack(storedItems[0]) }
    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = storedItems

    override fun getSlotsForFace(pSide: Direction): IntArray = when(pSide) {
        Direction.UP -> intArrayOf(0)
        Direction.DOWN -> intArrayOf(1)
        else -> intArrayOf()
    }

    override fun canPlaceItemThroughFace(pIndex: Int, pItemStack: ItemStack, pDirection: Direction?): Boolean =
        if(pDirection != null) getSlotsForFace(pDirection).contains(pIndex) else true
    override fun canTakeItemThroughFace(pIndex: Int, pStack: ItemStack, pDirection: Direction): Boolean =
        getSlotsForFace(pDirection).contains(pIndex)
}