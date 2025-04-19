package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeBuilder

class FluidEnergyBuilder(
	private val recipe: RecipeFunctionMulti,
	private val itemResults: List<Pair<Item, Int>> = listOf(),
	private val fluidResults: List<Pair<Fluid, Int>> = listOf()
) : BMRecipeBuilder.Multi() {
	override fun getResult(): Item = this.itemResults[0].first
	override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
		recipeOutput.accept(
			id,
			this.recipe.invoke(
				this.items,
				buildList {
					this@FluidEnergyBuilder.itemResults.forEach { this.add(ItemStack(it.first, it.second)) }
				}.toMutableList(),
				this.fluids,
				buildList {
					this@FluidEnergyBuilder.fluidResults.forEach { this.add(FluidStack(it.first, it.second)) }
				}.toMutableList(),
				this.time,
				this.energy
			), this.buildAdvancement(recipeOutput, id)
		)
	}
}