package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.gui.screens.BreadModScreen
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.BMContainerMenu
import org.bread_experts_group.breadmod.registry.menu.actual.LambdaSlotItemHandler
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe

class FluidEnergyMenu(
	type: MenuType<*>,
	id: Int,
	inventory: Inventory,
	entity: BreadModBlockEntity
) : BMContainerMenu.RecipeEntity<FluidEnergyRecipe>(
	type,
	id,
	inventory,
	entity
) {
	override val progressWidth: Int = -1
	override val containerSlotCount: Int = 8

	override fun ofScreen(title: Component): BreadModScreen<*> = FluidEnergyScreen(this, title)

	init {
		this.addInventorySlots(inventory, 8, 174, 116)
		val capability = this.entity.getCapability(Capabilities.ItemHandler.BLOCK)
		this.addSlot(LambdaSlotItemHandler(capability, 0, 15, 30))
		this.addSlot(LambdaSlotItemHandler(capability, 1, 32, 30))
		this.addSlot(LambdaSlotItemHandler(capability, 2, 15, 48))
		this.addSlot(LambdaSlotItemHandler(capability, 3, 32, 48))

		this.addSlot(LambdaSlotItemHandler.playerReadOnly(capability, 4, 103, 33))
		this.addSlot(LambdaSlotItemHandler.playerReadOnly(capability, 5, 129, 33))
		this.addSlot(LambdaSlotItemHandler.playerReadOnly(capability, 6, 103, 59))
		this.addSlot(LambdaSlotItemHandler.playerReadOnly(capability, 7, 129, 59))
	}
}