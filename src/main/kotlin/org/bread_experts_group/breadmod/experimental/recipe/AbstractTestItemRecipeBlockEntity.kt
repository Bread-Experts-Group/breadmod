package org.bread_experts_group.breadmod.experimental.recipe

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Container
import net.minecraft.world.ContainerHelper
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

abstract class AbstractTestItemRecipeBlockEntity<INPUT : RecipeInput, RECIPE : Recipe<INPUT>>(
	pos: BlockPos,
	state: BlockState,
	type: BlockEntityType<*>,
	recipeType: RecipeType<RECIPE>,
	private val slotCount: Int
) : AbstractTestRecipeBlockEntity<INPUT, RECIPE>(pos, state, type, recipeType), CraftingContainer {
	var itemSlots: NonNullList<ItemStack> = NonNullList.withSize(this.slotCount, ItemStack.EMPTY)

	// todo eventually migrate to using this instead of the vanilla method
//    val itemHandler = ItemStackHandler(4)
	override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.saveAdditional(tag, registries)

		ContainerHelper.saveAllItems(tag, this.itemSlots, registries)
	}

	override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
		super.loadAdditional(tag, registries)

		this.itemSlots = NonNullList.withSize(this.slotCount, ItemStack.EMPTY)
		ContainerHelper.loadAllItems(tag, this.itemSlots, registries)
	}

	override fun clearContent(): Unit = this.itemSlots.forEach { it.count = 0 }
	override fun getContainerSize(): Int = this.itemSlots.size
	override fun isEmpty(): Boolean = this.itemSlots.any { !it.isEmpty }
	override fun getItem(slot: Int): ItemStack = this.itemSlots[slot]
	override fun removeItem(slot: Int, pAmount: Int): ItemStack = this.itemSlots[slot].split(pAmount)
	override fun removeItemNoUpdate(slot: Int): ItemStack = this.itemSlots[slot].copyAndClear()
	override fun setItem(slot: Int, stack: ItemStack) {
		this.itemSlots[slot] = stack
	}

	override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)
	override fun fillStackedContents(contents: StackedContents) {
		for (stack: ItemStack in this.itemSlots) {
			contents.accountSimpleStack(stack)
		}
	}

	override fun getItems(): MutableList<ItemStack> = this.itemSlots
}