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
		this.addHandlerSlot(1, 32, 30)
		this.addHandlerSlot(2, 15, 48)
		this.addHandlerSlot(3, 32, 48)

		this.addResultHandlerSlot(4, 103, 33)
		this.addResultHandlerSlot(5, 129, 33)
		this.addResultHandlerSlot(6, 103, 59)
		this.addResultHandlerSlot(7, 129, 59)
	}

	override val containerSlotCount: Int = 8
}