package org.bread_experts_group.breadmod.experimental.recipe.block.multi.fluid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class MultiFluidRecipeMenu(
	id: Int,
	inventory: Inventory,
	parent: MultiFluidRecipeBlockEntity
) : AbstractTestRecipeMenu(ModMenuTypes.MULTI_FLUID.get(), id, inventory, parent) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.MULTI_FLUID_TEST.get())
			.get()
	)
}