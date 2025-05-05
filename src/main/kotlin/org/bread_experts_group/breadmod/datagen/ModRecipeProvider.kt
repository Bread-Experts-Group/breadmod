package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SpecialRecipeBuilder
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.ToastSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyBuilder
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(
	output: PackOutput,
	registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(output, registries) {
	override fun buildRecipes(recipeOutput: RecipeOutput) {
		ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.TEST_BREAD.toStack())
			.unlockedBy("has_item", RecipeProvider.has(Items.BREAD))
			.requires(Items.BREAD, 5)
			.save(recipeOutput, modLocation("special", "test"))

		SpecialRecipeBuilder.special { BreadSlicingRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "bread_slicing"))
		SpecialRecipeBuilder.special { ToastSlicingRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "toast_slicing"))
		// Toaster
		this.toasting(Items.BREAD, ModItems.TOASTED_BREAD.get(), recipeOutput, "bread_to_toasted_bread")
		this.toasting(ModItems.BREAD_SLICE.get(), ModItems.TOAST_SLICE.get(), recipeOutput, "slice_to_toast")
		this.toasting(ModItems.TOASTED_BREAD.get(), Items.CHARCOAL, recipeOutput, "toasted_bread_to_charcoal")
		this.toasting(ModItems.TOAST_SLICE.get(), Items.CHARCOAL, recipeOutput, "toast_slice_to_charcoal")
		// Wheat Crushing
		this.wheatCrushing(
			Items.WHEAT to 1,
			ModItems.FLOUR.get() to 2,
			5,
			2000,
			recipeOutput,
			"wheat_to_flour"
		)
		this.wheatCrushing(
			Items.HAY_BLOCK to 1,
			ModItems.FLOUR.get() to 18,
			15,
			6000,
			recipeOutput,
			"hay_block_to_flour"
		)
		// Dough Crafting
		this.doughCrafting(
			ModItems.FLOUR.get() to 1,
			Items.GUNPOWDER to 1,
			Fluids.WATER to 100,
			Items.TNT to 1,
			Fluids.LAVA to 500,
			10,
			5000,
			recipeOutput,
			"dough_machine_test"
		)
		// FluidEnergyRecipe
		FluidEnergyBuilder(
			::FluidEnergyRecipeTest,
			listOf(Items.BREAD to 16),
			listOf(Fluids.WATER to 500)
		)
			.itemRequired(ModItems.FLOUR.get(), 8)
			.itemRequired(Items.APPLE, 8)
			.fluidRequired(Fluids.LAVA, 500)
			.timeRequired(100)
			.save(recipeOutput, modLocation("fluid_energy", "test_one"))
		FluidEnergyBuilder(
			::FluidEnergyRecipeTest,
			listOf(Items.COOKED_BEEF to 16)
		)
			.itemRequired(ModItems.FLOUR.get(), 8)
			.itemRequired(Items.SPONGE, 8)
			.timeRequired(100)
			.save(recipeOutput, modLocation("fluid_energy", "test_two"))

		FluidEnergyBuilder(
			::FluidEnergyRecipeTest,
			listOf(Items.BREAD to 16, Items.STRING to 16)
		)
			.itemRequired(ModItems.FLOUR.get(), 8)
			.itemRequired(Items.REDSTONE, 8)
			.timeRequired(50)
			.save(recipeOutput, modLocation("fluid_energy", "test_three"))
	}

	private fun toasting(input: Item, result: Item, output: RecipeOutput, name: String): Unit =
		FluidEnergyBuilder(::ToasterRecipe, listOf(result to 2))
			.itemRequired(input, 2)
			.timeRequiredInSeconds(5)
			.save(output, modLocation("machine", "toasting", name))

	private fun wheatCrushing(
		input: Pair<Item, Int>,
		result: Pair<Item, Int>,
		seconds: Int,
		energy: Int,
		output: RecipeOutput,
		name: String
	): Unit = FluidEnergyBuilder(::WheatCrusherRecipe, listOf(result))
		.itemRequired(input.first, input.second)
		.timeRequiredInSeconds(seconds)
		.energyRequired(energy)
		.save(output, modLocation("machine", "wheat_crushing", name))

	private fun doughCrafting(
		inputOne: Pair<Item, Int>,
		inputTwo: Pair<Item, Int>,
		fluidInput: Pair<Fluid, Int>,
		itemOutput: Pair<Item, Int>,
		fluidOutput: Pair<Fluid, Int>,
		seconds: Int,
		energy: Int,
		output: RecipeOutput,
		name: String
	): Unit = FluidEnergyBuilder(::DoughMachineRecipe, listOf(itemOutput), listOf(fluidOutput))
		.itemRequired(inputOne.first, inputOne.second)
		.itemRequired(inputTwo.first, inputTwo.second)
		.fluidRequired(fluidInput.first, fluidInput.second)
		.timeRequiredInSeconds(seconds)
		.energyRequired(energy)
		.save(output, modLocation("machine", "dough_crafting", name))
}