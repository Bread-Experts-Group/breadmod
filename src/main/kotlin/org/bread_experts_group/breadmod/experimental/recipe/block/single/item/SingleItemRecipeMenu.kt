package org.bread_experts_group.breadmod.experimental.recipe.block.single.item

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.ResultSlot

class SingleItemRecipeMenu(
	id : Int,
	inventory : Inventory,
	parent : SingleItemRecipeBlockEntity
) : AbstractTestRecipeMenu(ModMenuTypes.SINGLE_ITEM.get(), id, inventory, parent) {
	constructor(id : Int, inventory : Inventory, byteBuf : RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.SINGLE_ITEM_TEST.get())
			.get()
	)

	init {
		this.addSlot(Slot(parent, 0, 15, 30))
		this.addSlot(ResultSlot(1, 40, 30, parent))
	}

	override val containerSlotCount : Int = 2
}