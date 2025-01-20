package org.bread_experts_group.breadmod.experimental.recipe.block.single.fluid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractTestRecipeMenu
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class SingleFluidRecipeMenu(
	id: Int,
	inventory: Inventory,
	parent: SingleFluidRecipeBlockEntity
) : AbstractTestRecipeMenu<
		BMRecipeInputs.SingleFluid,
		SingleFluidTestRecipe,
		SingleFluidRecipeBlockEntity,
		SingleFluidRecipeMenu
		>(ModMenuTypes.SINGLE_FLUID.get(), id, inventory, parent) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.SINGLE_FLUID_TEST.get())
			.get()
	)
}