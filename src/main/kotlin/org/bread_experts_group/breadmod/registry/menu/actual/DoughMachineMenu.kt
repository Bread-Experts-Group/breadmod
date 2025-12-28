package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.chat.Component
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.gui.screens.BreadModScreen
import org.bread_experts_group.breadmod.client.gui.screens.DoughMachineScreen
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe

class DoughMachineMenu(
	type: MenuType<*>,
	id: Int,
	inventory: Inventory,
	entity: BreadModBlockEntity
) : BMContainerMenu.RecipeEntity<DoughMachineRecipe>(
	type,
	id,
	inventory,
	entity
) {
	override val progressWidth: Int = 24
	override val containerSlotCount: Int = 4
	override fun ofScreen(title: Component): BreadModScreen<*> = DoughMachineScreen(this, title)

	init {
		this.addInventorySlots(inventory, 8, 142, 84)
		val capability = this.entity.getCapability(Capabilities.ItemHandler.BLOCK)
		this.addSlot(LambdaSlotItemHandler(capability, 0, 10, 34))
		this.addSlot(LambdaSlotItemHandler(capability, 1, 45, 34))
		this.addSlot(LambdaSlotItemHandler.playerReadOnly(capability, 2, 98, 35))
		this.addSlot(
			LambdaSlotItemHandler.fluidHandlerOnly(capability, 3, 153, 7)
			{ it.`is`(FluidTags.WATER) }
		)
	}
}