package org.bread_experts_group.breadmod.experimental

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeManager
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import java.util.*

abstract class AbstractTestItemRecipeBlockEntity<INPUT : RecipeInput, RECIPE : Recipe<INPUT>>(
    pos: BlockPos,
    state: BlockState,
    type: BlockEntityType<*>,
    recipeType: RecipeType<RECIPE>,
    slotCount: Int
) : BlockEntity(type, pos, state), CraftingContainer, MenuProvider {
    var progress = 0
    var maxProgress = 0

    var currentRecipe: Optional<RECIPE> = Optional.empty()
    val recipeDial: RecipeManager.CachedCheck<INPUT, RECIPE> by lazy {
        RecipeManager.createCheck(recipeType)
    }

    var itemSlots: NonNullList<ItemStack> = NonNullList.withSize(slotCount, ItemStack.EMPTY)

    abstract fun tick(level: Level, pos: BlockPos, state: BlockState)

    abstract fun recipeDone(recipe: RECIPE, level: Level)

    /**
     * Resets the current recipe.
     */
    fun resetRecipe() {
        currentRecipe = Optional.empty()
        maxProgress = 0; progress = 0
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

    override fun getItems(): MutableList<ItemStack> = itemSlots

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag =
        super.getUpdateTag(registries).also { saveAdditional(it, registries) }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)
}