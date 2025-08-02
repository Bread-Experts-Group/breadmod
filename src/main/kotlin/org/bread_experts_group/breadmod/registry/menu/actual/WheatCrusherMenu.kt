package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.MenuType
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.client.gui.screens.BreadModScreen
import org.bread_experts_group.breadmod.client.gui.screens.WheatCrusherScreen
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe

class WheatCrusherMenu(
	type: MenuType<*>,
	id: Int,
	inventory: Inventory,
	parent: BreadModBlockEntity
) : BMContainerMenu.RecipeEntity<WheatCrusherRecipe>(
	type,
	id,
	inventory,
	parent
) {
	override val progressWidth: Int = 48
	override val containerSlotCount: Int = 2
	override fun ofScreen(title: Component): BreadModScreen = WheatCrusherScreen(this, title)

	init {
		this.addInventorySlots(inventory, 8, 174, 116)
		val capability = this.entity.getCapability(Capabilities.ItemHandler.BLOCK)
		this.addSlot(LambdaSlotItemHandler(capability, 0, 80, 15))
		this.addSlot(LambdaSlotItemHandler.playerReadOnly(capability, 1, 80, 87))
	}
}