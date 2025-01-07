package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.AbstractModContainerMenu
import org.bread_experts_group.breadmod.registry.menu.actual.ResultSlotItemHandler

class FluidEnergyMenu(
	id : Int,
	inventory : Inventory,
	val parent : FluidEnergyBlockEntity
) : AbstractModContainerMenu(ModMenuTypes.FLUID_ENERGY_TEST.get(), id) {
	constructor(id : Int, inventory : Inventory, byteBuf : RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.FLUID_ENERGY.get()).get()
	)

	init {
		this.addInventorySlots(inventory, 8, 174, 116)

		if (this.parent.items != null) {
			this.addSlot(SlotItemHandler(this.parent.items, 0, 15, 30))
			this.addSlot(SlotItemHandler(this.parent.items, 1, 30, 30))
			this.addSlot(SlotItemHandler(this.parent.items, 2, 15, 45))
			this.addSlot(SlotItemHandler(this.parent.items, 3, 30, 45))

			this.addSlot(ResultSlotItemHandler(this.parent.items, 4, 60, 30))
			this.addSlot(ResultSlotItemHandler(this.parent.items, 5, 75, 30))
			this.addSlot(ResultSlotItemHandler(this.parent.items, 6, 60, 45))
			this.addSlot(ResultSlotItemHandler(this.parent.items, 7, 75, 45))
		}
	}

	override val containerSlotCount : Int = 8
}