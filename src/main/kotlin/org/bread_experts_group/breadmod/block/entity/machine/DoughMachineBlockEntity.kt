package org.bread_experts_group.breadmod.block.entity.machine

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
import net.minecraft.world.MenuProvider
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.energy.EnergyStorage
import net.neoforged.neoforge.fluids.capability.templates.FluidTank
import net.neoforged.neoforge.items.wrapper.SidedInvWrapper
import org.bread_experts_group.breadmod.Breadmod.Companion.LOGGER
import org.bread_experts_group.breadmod.Breadmod.Companion.modTranslatable
import org.bread_experts_group.breadmod.menu.DoughMachineMenu
import org.bread_experts_group.breadmod.recipe.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import java.util.*
import kotlin.math.max

class DoughMachineBlockEntity(
    pos: BlockPos, state: BlockState
) : BlockEntity(
    ModBlockEntityTypes.DOUGH_MACHINE.get(),
    pos,
    state
), MenuProvider, CraftingContainer, WorldlyContainer {
    private companion object {
        var debugMode = true
    }

    var progress = 0
    var maxProgress = 0
    private var energyDivision: Int? = null

    var currentRecipe: Optional<FluidEnergyRecipe> = Optional.empty()
    val recipeDial: RecipeManager.CachedCheck<FluidEnergyRecipe.FluidEnergyInput, FluidEnergyRecipe> by lazy {
        RecipeManager.createCheck(ModRecipeTypes.FLUID_ENERGY.get())
    }

    val energyHandler: EnergyStorage by lazy {
        object : EnergyStorage(100000) {
            override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int {
                syncToClients()
                return super.receiveEnergy(toReceive, simulate)
            }
        }
    }

    val fluidHandler: FluidTank by lazy {
        object : FluidTank(10000) {
            override fun onContentsChanged() {
                syncToClients()
            }

            override fun getTanks(): Int = 2
        }
    }

    fun getFluidtank(index: Int) = fluidHandler.getFluidInTank(index)

    val horizontal = this.blockState.getValue(HorizontalDirectionalBlock.FACING)
    val sidedInvWrapper = SidedInvWrapper(this, horizontal)

    var itemSlots: NonNullList<ItemStack> = NonNullList.withSize(3, ItemStack.EMPTY)

    private fun syncToClients() = level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_CLIENTS)

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: DoughMachineBlockEntity) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!inputStillValid(activeRecipe)) resetRecipe()
            if (!inputStillValid(activeRecipe)) resetRecipe()
            val div = if (energyDivision == null) ((activeRecipe.energy) / max(
                activeRecipe.time,
                1
            )).also { div -> energyDivision = div } else energyDivision ?: return@ifPresentOrElse
            if ((div < 0) && (energyHandler.energyStored + div > energyHandler.maxEnergyStored)) return@ifPresentOrElse
            val energy = energyHandler.extractEnergy(div, false)

            if (energy >= div && canFitResults(activeRecipe)) {
                progress++
                level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true))
                if (progress >= activeRecipe.time) {
                    recipeDone(activeRecipe)
                    resetRecipe()
                }
            }
        }, {
            val check = recipeDial.getRecipeFor(
                FluidEnergyRecipe.FluidEnergyInput(
                    2,
                    listOf(getItem(0)),
                    listOf()
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                println("recipe present?")
                if (!canFitResults(recipe)) return@ifPresent
                currentRecipe = Optional.of(recipe)
                maxProgress = recipe.time

                if (debugMode) {
                    try {
                        LOGGER.info("after getting recipe: $recipe")
                        LOGGER.info("recipe id: ${present.id.path}")
                        LOGGER.info("item requirement list: ${present.value.itemIngredients}")
                        LOGGER.info("item output list: ${present.value.results}")
                        LOGGER.info("fluid requirement list: ${present.value.fluidIngredients}")
                        LOGGER.info("fluid output list: ${present.value.fluidResults}")
                        LOGGER.info("time required: ${present.value.time}")
                    } catch (e: Exception) {
                        LOGGER.error(e)
                    }
                }
            }
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, false))
        })
    }

    fun resetRecipe() {
        level?.setBlockAndUpdate(blockPos, blockState.setValue(BlockStateProperties.POWERED, false))
        currentRecipe = Optional.empty()
        maxProgress = 0; progress = -1
        energyDivision = null
    }

    // in this machine's case we only have one input fluid tank
    fun inputStillValid(recipe: FluidEnergyRecipe): Boolean {
        val item = (getItem(0) == recipe.getFirstItem()) || items.isNotEmpty()
        val fluid = (recipe.getFirstFluid() == getFluidtank(0)) || !fluidHandler.isEmpty

        return item && fluid
    }

    fun canFitResults(recipe: FluidEnergyRecipe): Boolean =
        (getItem(1).count < maxStackSize || getItem(1).count + recipe.getFirstItem().count < maxStackSize) &&
                getFluidtank(1).amount < fluidHandler.getTankCapacity(1) ||
                getFluidtank(1).amount + recipe.getFirstFluid().amount < fluidHandler.getTankCapacity(1)

    fun recipeDone(recipe: FluidEnergyRecipe) {
        itemSlots[0].shrink(recipe.getFirstItem().count)
        getFluidtank(0).shrink(recipe.getFirstFluid().amount)
        val assembleItems = recipe.assembleItems(
            FluidEnergyRecipe.FluidEnergyInput(
                2,
                itemSlots,
                listOf(getFluidtank(0))
            )
        )
        val assembleFluids = recipe.assembleFluids(
            FluidEnergyRecipe.FluidEnergyInput(
                2,
                itemSlots,
                listOf(getFluidtank(1))
            )
        )

        if (itemSlots[1].isEmpty) itemSlots[1] =
            assembleItems[0].copyWithCount(recipe.results[0].count) else itemSlots[1].grow(recipe.results[0].count)
        if (getFluidtank(1).isEmpty) fluidHandler.fluid =
            assembleFluids[0].copyWithAmount(recipe.fluidResults[0].amount) else
            getFluidtank(1).amount += recipe.fluidResults[0].amount
    }

    override fun clearContent() = itemSlots.forEach { it.count = 0 }

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
    override fun getSlotsForFace(side: Direction): IntArray = intArrayOf(0, 1, 2)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        if (direction != null) getSlotsForFace(direction).contains(index) && index == 0 else true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
        getSlotsForFace(direction).contains(index) && ((index == 2 && stack.`is`(Items.BUCKET)) || index != 0)

    override fun getWidth(): Int = 1
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = itemSlots

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        DoughMachineMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = modTranslatable("block", "dough_machine")
}