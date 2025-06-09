package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe

class DoughMachineMenu(
	id: Int,
	inventory: Inventory,
	parent: DoughMachineBlockEntity
) : BMContainerMenu.RecipeEntity<DoughMachineRecipe, DoughMachineBlockEntity>(
	ModMenuTypes.DOUGH_MACHINE.get(),
	id,
	inventory,
	parent
) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		BMContainerMenu.blockEntityFromByteBuf(inventory, byteBuf, ModBlockEntityTypes.DOUGH_MACHINE)
	)

	override val progressWidth: Int = 24
	override val containerSlotCount: Int = 4

	init {
		this.addInventorySlots(inventory, 8, 142, 84)
		this.addHandlerSlot(0, 10, 34)
		this.addHandlerSlot(1, 45, 34)
		this.addResultHandlerSlot(2, 98, 35)
		this.addSlot(BucketSlot(this.parent.itemHandler))
	}
}