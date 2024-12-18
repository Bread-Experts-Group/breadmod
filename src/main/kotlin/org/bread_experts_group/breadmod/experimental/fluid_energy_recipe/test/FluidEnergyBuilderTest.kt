package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.test

import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyRecipe

class FluidEnergyBuilderTest(
    itemResults: List<Pair<Item, Int>>,
    fluidResults: List<Pair<Fluid, Int>>
) : FluidEnergyRecipe.FluidEnergyBuilder(itemResults, fluidResults) {
    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val recipe = FluidEnergyRecipeTest(
            items,
            buildList { itemResults.forEach { add(ItemStack(it.first, it.second)) } },
            fluids,
            buildList { fluidResults.forEach { add(FluidStack(it.first, it.second)) } },
            time,
            energy
        )
        recipeOutput.accept(id, recipe, buildAdvancement(recipeOutput, id))
    }
}