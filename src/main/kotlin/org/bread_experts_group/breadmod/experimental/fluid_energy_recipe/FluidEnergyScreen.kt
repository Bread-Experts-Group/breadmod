package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.experimental.recipe.AbstractRecipeScreen

class FluidEnergyScreen(
	menu : FluidEnergyMenu,
	inventory : Inventory,
	title : Component
) : AbstractRecipeScreen<FluidEnergyMenu>(menu, inventory, title)