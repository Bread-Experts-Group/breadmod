package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.FERecipeHandler
import org.bread_experts_group.breadmod.registry.menu.BreadModMenu
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe

abstract class BMContainerMenu(
	type: MenuType<*>,
	id: Int,
	inventory: Inventory,
	entity: BreadModBlockEntity
) : BreadModMenu(type, id, inventory, entity) {
	protected companion object {
		// CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
		// must assign a slot number to each of the slots used by the GUI.
		// For this container, we can see both the tile inventory's slots and the player inventory slots and the hotbar.
		// Each time we add a Slot to the container, it automatically increases the slotIndex, which means
		//  0 – 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 – 8)
		//  9 – 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 – 35)
		//  36 – 44 = TileInventory slots, which map to our BlockEntity slot numbers 0 – 8)
		private const val HOTBAR_SLOT_COUNT: Int = 9
		private const val PLAYER_INVENTORY_ROW_COUNT: Int = 3
		private const val PLAYER_INVENTORY_COLUMN_COUNT: Int = 9
		private const val PLAYER_INVENTORY_SLOT_COUNT: Int =
			this.PLAYER_INVENTORY_COLUMN_COUNT * this.PLAYER_INVENTORY_ROW_COUNT
		private const val VANILLA_SLOT_COUNT: Int = this.HOTBAR_SLOT_COUNT + this.PLAYER_INVENTORY_SLOT_COUNT
		private const val VANILLA_FIRST_SLOT_INDEX: Int = 0
		private const val TE_INVENTORY_FIRST_SLOT_INDEX: Int = this.VANILLA_FIRST_SLOT_INDEX + this.VANILLA_SLOT_COUNT
	}

	protected fun addInventorySlots(inventory: Inventory, pX: Int, hotBarY: Int, inventoryY: Int) {
		repeat(9) { this.addSlot(Slot(inventory, it, 8 + it * 18, hotBarY)) }
		repeat(3) { y ->
			repeat(9) { x ->
				this.addSlot(
					Slot(
						inventory,
						x + y * 9 + 9,
						pX + x * 18,
						inventoryY + y * 18
					)
				)
			}
		}
	}

	override fun stillValid(player: Player): Boolean = true

	/**
	 * ### Used in [quickMoveStack] to enable shift clicking items into the target inventory
	 * value must match the number of slots your block entity has
	 */
	open val containerSlotCount: Int = 0
	private fun moveStackFunction(playerIn: Player, pIndex: Int): ItemStack {
		val sourceSlot = this.slots[pIndex]
		if (!sourceSlot.hasItem()) return ItemStack.EMPTY //EMPTY_ITEM
		val sourceStack = sourceSlot.item
		// ENSURE that expansible stacks aren't exposed to the player inventory!
//		sourceStack.remove(ModDataComponents.EXPANSIBLE_ITEM_STACK)
		val copyOfSourceStack = sourceStack.copy()
		// Check if the slot clicked is one of the vanilla container slots
		if (pIndex < Companion.VANILLA_FIRST_SLOT_INDEX + Companion.VANILLA_SLOT_COUNT) {
			// This is a vanilla container slot so merge the stack into the tile inventory
			if (!this.moveItemStackTo(
					sourceStack,
					Companion.TE_INVENTORY_FIRST_SLOT_INDEX,
					Companion.TE_INVENTORY_FIRST_SLOT_INDEX + this.containerSlotCount,
					false
				)
			) return ItemStack.EMPTY // EMPTY_ITEM
		} else if (pIndex < Companion.TE_INVENTORY_FIRST_SLOT_INDEX + this.containerSlotCount) {
			// This is a BE slot so merge the stack into the player's inventory
			if (!this.moveItemStackTo(
					sourceStack,
					Companion.VANILLA_FIRST_SLOT_INDEX,
					Companion.VANILLA_FIRST_SLOT_INDEX + Companion.VANILLA_SLOT_COUNT,
					false
				)
			) return ItemStack.EMPTY
		} else return ItemStack.EMPTY
		// If stack size == 0 (the entire stack was moved) set slot contents to null
		if (sourceStack.count == 0) {
			sourceSlot.set(ItemStack.EMPTY)
		} else sourceSlot.setChanged()
		sourceSlot.onTake(playerIn, sourceStack)
		return copyOfSourceStack
	}

	override fun quickMoveStack(player: Player, index: Int): ItemStack = this.moveStackFunction(player, index)

	abstract class Entity(
		type: MenuType<*>,
		id: Int,
		inventory: Inventory,
		entity: BreadModBlockEntity
	) : BMContainerMenu(type, id, inventory, entity) {
		abstract override val containerSlotCount: Int
		override fun stillValid(player: Player): Boolean = player.containerMenu == this
	}

	abstract class RecipeEntity<R : FluidEnergyRecipe>(
		type: MenuType<*>,
		id: Int,
		inventory: Inventory,
		entity: BreadModBlockEntity
	) : Entity(type, id, inventory, entity) {
		open val progressWidth: Int = 0
		val scaledProgress: Int
			get() {
				val recipeHandler = this.entity.getCapabilityOrNull(FERecipeHandler.BLOCK_VOID) ?: return 0
				val time = recipeHandler.recipe?.value?.rTime ?: recipeHandler.progress
				return ((recipeHandler.progress.toFloat() / time.toFloat()) * this.progressWidth).toInt()
			}

		fun isCrafting(): Boolean {
			val recipeHandler = this.entity.getCapabilityOrNull(FERecipeHandler.BLOCK_VOID) ?: return false
			return recipeHandler.progress > 0u
		}
	}
}