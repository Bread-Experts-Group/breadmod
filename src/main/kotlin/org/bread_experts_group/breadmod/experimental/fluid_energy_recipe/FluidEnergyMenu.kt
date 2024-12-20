package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.ResultSlot

class FluidEnergyMenu(
	id : Int,
	inventory : Inventory,
	parent : FluidEnergyBlockEntity
) : AbstractTestRecipeMenu(ModMenuTypes.FLUID_ENERGY_TEST.get(), id, inventory, parent) {
	constructor(id : Int, inventory : Inventory, byteBuf : RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.FLUID_ENERGY.get()).get()
	)

	init {
		this.addSlot(Slot(parent, 0, 15, 30))
		this.addSlot(Slot(parent, 1, 30, 30))
		this.addSlot(Slot(parent, 2, 15, 45))
		this.addSlot(Slot(parent, 3, 30, 45))

		this.addSlot(ResultSlot(4, 60, 30, parent))
		this.addSlot(ResultSlot(5, 75, 30, parent))
		this.addSlot(ResultSlot(6, 60, 45, parent))
		this.addSlot(ResultSlot(7, 75, 45, parent))
	}

	override val containerSlotCount : Int = 8
}