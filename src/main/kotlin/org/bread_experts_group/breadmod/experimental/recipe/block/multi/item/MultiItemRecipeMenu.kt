package org.bread_experts_group.breadmod.experimental.recipe.block.multi.item

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.menu.actual.ResultSlot

class MultiItemRecipeMenu(
	id: Int,
	inventory: Inventory,
	parent: MultiItemRecipeBlockEntity
) : AbstractTestRecipeMenu<
		BMRecipeInputs.MultiItem,
		MultiItemTestRecipe,
		MultiItemRecipeBlockEntity,
		MultiItemRecipeMenu
		>(ModMenuTypes.MULTI_ITEM.get(), id, inventory, parent) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.MULTI_ITEM_TEST.get()).get()
	)

	init {
		this.addSlot(Slot(parent, 0, 15, 30))
		this.addSlot(Slot(parent, 1, 30, 30))
		this.addSlot(Slot(parent, 2, 45, 30))
		this.addSlot(ResultSlot(3, 80, 30, parent))
	}

	override val containerSlotCount: Int = 4
}