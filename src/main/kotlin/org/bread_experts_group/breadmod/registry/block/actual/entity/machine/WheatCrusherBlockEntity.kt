package org.bread_experts_group.breadmod.registry.block.actual.entity.machine

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
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
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper
import org.bread_experts_group.breadmod.BreadMod.Companion.LOGGER
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe.WheatCrusherInput
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*
import kotlin.math.max

class WheatCrusherBlockEntity(
    pos: BlockPos, state: BlockState
) : BlockEntity(
    ModBlockEntityTypes.WHEAT_CRUSHER.get(),
    pos,
    state
), MenuProvider, CraftingContainer, WorldlyContainer {
    private companion object {
        var debugMode = false
    }

    var progress: Int = 0
    var maxProgress: Int = 0
    private var energyDivision: Int? = null

    private var currentRecipe: Optional<WheatCrusherRecipe> = Optional.empty()
    private val recipeDial: RecipeManager.CachedCheck<WheatCrusherInput, WheatCrusherRecipe> by lazy {
        RecipeManager.createCheck(ModRecipeTypes.WHEAT_CRUSHING.get())
    }

    val energyHandler: EnergyStorage by lazy {
        object : EnergyStorage(100000) {
            // todo there's gotta be a better way to sync the energy every receive and extract cause this just seems hacky
            override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
                syncToClients()
                return super.receiveEnergy(toReceive, simulate)
            }
        }
    }

    val horizontal: Direction? = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
    val sidedInvWrapper: SidedInvWrapper = SidedInvWrapper(this, horizontal)

    private var itemSlots: NonNullList<ItemStack> = NonNullList.withSize(2, ItemStack.EMPTY)

    // todo having syncToClients() not be present in extract and receive in the energy handler stops it from syncing every time it handles energy
    //  then the energy is only updated when tick() updates the block on client
    //  the caveat is having syncToClients() fire every energy event is potentially bogging down game resources and the meter in the gui being jittery
    private fun syncToClients() = level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)

    /**
     * Ticks the recipe logic for this [BlockEntity]
     */
    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: WheatCrusherBlockEntity) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!inputStillValid(activeRecipe)) resetRecipe()
            val div = if (energyDivision == null) ((activeRecipe.recipeEnergy) / max(
                activeRecipe.recipeTime,
                1
            )).also { div -> energyDivision = div } else energyDivision ?: return@ifPresentOrElse
            if ((div < 0) && (energyHandler.energyStored + div > energyHandler.maxEnergyStored)) return@ifPresentOrElse
            val energy = energyHandler.extractEnergy(div, false)

            if (energy >= div && canFitResults(activeRecipe)) {
                progress++
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true))
                if (progress >= activeRecipe.recipeTime) {
                    recipeDone(level, activeRecipe)
                    resetRecipe()
                }
            }
        }, {
            val check = recipeDial.getRecipeFor(WheatCrusherInput(getItem(0), getItem(0).count), level)

            check.ifPresent { present ->
                val recipe = present.value
                if (!canFitResults(recipe)) return@ifPresent
                currentRecipe = Optional.of(recipe)
                maxProgress = recipe.recipeTime

                if (debugMode) {
                    try {
                        LOGGER.info("after getting recipe: $recipe")
                        LOGGER.info("recipe id: ${present.id.path}")
                        LOGGER.info("item requirement: ${present.value.recipeInput}")
                        LOGGER.info("item output: ${present.value.recipeOutput}")
                        LOGGER.info("time required: ${present.value.recipeTime}")
                    } catch (e: Exception) {
                        LOGGER.error(e)
                    }
                }
            }
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false))
        })
    }

    private fun resetRecipe() {
        level?.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.POWERED, false))
        currentRecipe = Optional.empty()
        maxProgress = 0; progress = -1
        energyDivision = null
    }

    private fun inputStillValid(recipe: WheatCrusherRecipe): Boolean =
        getItem(0) == recipe.recipeInput || !getItem(0).isEmpty

    private fun canFitResults(recipe: WheatCrusherRecipe): Boolean =
        getItem(1).count < maxStackSize || getItem(1).count + recipe.recipeOutput.count < maxStackSize

    private fun recipeDone(level: Level, recipe: WheatCrusherRecipe) {
        itemSlots[0].shrink(recipe.recipeInput.count)
        val assemble =
            recipe.assemble(WheatCrusherInput(getItem(0), recipe.recipeInput.count), level.registryAccess())
        if (itemSlots[1].isEmpty) itemSlots[1] =
            assemble.copyWithCount(recipe.recipeOutput.count) else itemSlots[1].grow(recipe.recipeOutput.count)
    }

    override fun getMaxStackSize(): Int = 64

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.put("energy", energyHandler.serializeNBT(registries))
        tag.putInt("progress", progress)
        tag.putInt("maxProgress", maxProgress)

        ContainerHelper.saveAllItems(tag, itemSlots, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        energyHandler.deserializeNBT(registries, tag.get("energy") ?: return)
        progress = tag.getInt("progress")
        maxProgress = tag.getInt("maxProgress")

        itemSlots = NonNullList.withSize(2, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, itemSlots, registries)
    }

    override fun clearContent(): Unit = itemSlots.forEach { it.count = 0 }

    override fun getContainerSize(): Int = itemSlots.size
    override fun isEmpty(): Boolean = itemSlots.any { !it.isEmpty }
    override fun getItem(slot: Int): ItemStack = itemSlots[slot]
    override fun removeItem(slot: Int, pAmount: Int): ItemStack = itemSlots[slot].split(pAmount)
    override fun removeItemNoUpdate(slot: Int): ItemStack = itemSlots[slot].copyAndClear()
    override fun setItem(slot: Int, stack: ItemStack) {
        itemSlots[slot] = stack
    }

    override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)

    override fun fillStackedContents(contents: StackedContents) {
        for (stack: ItemStack in itemSlots) {
            contents.accountSimpleStack(stack)
        }
    }

    // allow every face of the block to receive and extract items
    override fun getSlotsForFace(side: Direction): IntArray = intArrayOf(0, 1)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        if (direction != null) getSlotsForFace(direction).contains(index) && index == 0 else true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
        getSlotsForFace(direction).contains(index) && index != 0

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = itemSlots

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        WheatCrusherMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = modTranslatable("block", "wheat_crusher")
}