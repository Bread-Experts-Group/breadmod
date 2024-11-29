package org.bread_experts_group.breadmod.registry.block.actual.experimental.multi_item

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
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.items.wrapper.InvWrapper
import org.bread_experts_group.breadmod.BreadMod.Companion.LOGGER
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.experimental.BMRecipeInputs
import org.bread_experts_group.breadmod.registry.recipe.actual.experimental.multi.MultiItemTestRecipe
import java.util.*

class MultiItemRecipeBlockEntity(
    pos: BlockPos,
    state: BlockState
) : BlockEntity(ModBlockEntityTypes.MULTI_ITEM_TEST.get(), pos, state), CraftingContainer, WorldlyContainer,
    MenuProvider {
    var progress = 0
    var maxProgress = 0

    var currentRecipe: Optional<MultiItemTestRecipe> = Optional.empty()
    val recipeDial: RecipeManager.CachedCheck<BMRecipeInputs.MultiItem, MultiItemTestRecipe> by lazy {
        RecipeManager.createCheck(ModRecipeTypes.MULTI_ITEM.get())
    }

    val invWrapper = InvWrapper(this)
    var itemSlots: NonNullList<ItemStack> = NonNullList.withSize(4, ItemStack.EMPTY)

    fun tick(level: Level, pos: BlockPos, state: BlockState, blockEntity: MultiItemRecipeBlockEntity) {
        currentRecipe.ifPresentOrElse({ activeRecipe ->
            if (!activeRecipe.inputStillValid(items)) resetRecipe()
            val recipeTime = activeRecipe.rTime ?: 0
            progress++
            if (progress >= recipeTime) {
                recipeDone(activeRecipe)
                resetRecipe()
            }
        }, {
            val check = recipeDial.getRecipeFor(
                BMRecipeInputs.MultiItem(
                    items,
                    buildList { items.forEach { add(it.count) } },
                    3
                ), level
            )

            check.ifPresent { present ->
                val recipe = present.value
                val recipeTime = recipe.rTime ?: 0
                currentRecipe = Optional.of(recipe)
                maxProgress = recipeTime

                try {
                    LOGGER.info("recipe id: ${present.id.path}")
                    LOGGER.info("items: ${recipe.rItemInputs}")
                    LOGGER.info("outputs: ${recipe.rItemOutputs}")
                } catch (e: Exception) {
                    LOGGER.error(e)
                }
            }
        })
    }

    fun recipeDone(recipe: MultiItemTestRecipe) {
        val assemble = recipe.assembleOutputs(
            BMRecipeInputs.MultiItem(
                items,
                buildList { items.forEach { add(it.count) } },
                3
            )
        )
        if (itemSlots[3].isEmpty) itemSlots[3] =
            assemble[0].copyWithCount(recipe.rItemOutputs[0].count) else itemSlots[3].grow(recipe.rItemOutputs[0].count)
        recipe.consumeInputs(items)
    }

    /**
     * Resets the current recipe.
     */
    fun resetRecipe() {
        currentRecipe = Optional.empty()
        maxProgress = 0; progress = -1
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("progress", progress)
        tag.putInt("maxProgress", maxProgress)

        ContainerHelper.saveAllItems(tag, itemSlots, registries)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        progress = tag.getInt("progress")
        maxProgress = tag.getInt("maxProgress")

        itemSlots = NonNullList.withSize(4, ItemStack.EMPTY)
        ContainerHelper.loadAllItems(tag, itemSlots, registries)
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
    override fun getSlotsForFace(side: Direction): IntArray = intArrayOf(0, 1, 2, 3)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        if (direction != null) getSlotsForFace(direction).contains(index) && index != 3 else true

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
        getSlotsForFace(direction).contains(index) && index == 3

    override fun getWidth(): Int = 3
    override fun getHeight(): Int = 1
    override fun getItems(): MutableList<ItemStack> = itemSlots

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun createMenu(containerId: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu =
        MultiItemRecipeMenu(containerId, playerInventory, this)

    override fun getDisplayName(): Component = Component.literal("multi item recipe test block")
}