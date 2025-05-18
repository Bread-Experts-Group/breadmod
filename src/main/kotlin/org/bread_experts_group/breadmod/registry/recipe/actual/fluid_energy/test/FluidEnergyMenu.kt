package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.BMContainerMenu

class FluidEnergyMenu(
	id: Int,
	inventory: Inventory,
	parent: FluidEnergyBlockEntity
) : BMContainerMenu.RecipeEntity<FluidEnergyRecipeTest, FluidEnergyBlockEntity>(
	ModMenuTypes.FLUID_ENERGY_TEST.get(),
	id,
	inventory,
	parent
) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		BMContainerMenu.blockEntityFromByteBuf(inventory, byteBuf, ModBlockEntityTypes.FLUID_ENERGY)
	)

	init {
		this.addInventorySlots(inventory, 8, 174, 116)

		this.addHandlerSlot(0, 15, 30)
		this.addHandlerSlot(1, 30, 30)
		this.addHandlerSlot(2, 15, 45)
		this.addHandlerSlot(3, 30, 45)

		this.addResultHandlerSlot(4, 60, 30)
		this.addResultHandlerSlot(5, 75, 30)
		this.addResultHandlerSlot(6, 60, 45)
		this.addResultHandlerSlot(7, 75, 45)
	}

	override val containerSlotCount: Int = 8
}