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
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyBuilder
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import java.util.concurrent.CompletableFuture

typealias ItemResults = List<ItemResult>
typealias ItemResult = Pair<Item, Int>
typealias FluidResults = List<FluidResult>
typealias FluidResult = Pair<Fluid, Int>

class ModRecipeProvider(
	output: PackOutput,
	registries: CompletableFuture<HolderLookup.Provider>
) : RecipeProvider(output, registries) {
	override fun buildRecipes(recipeOutput: RecipeOutput) {
		FluidEnergyBuilder(
			::WheatCrusherRecipe,
			listOf(ModItems.FLOUR.get() to 2)
		)
			.itemRequired(Items.WHEAT)
			.timeRequired(100)
			.energyRequired(2000)
			.save(recipeOutput, modLocation("special", "machine", "wheat_crushing"))

		ShapelessRecipeBuilder(RecipeCategory.MISC, ModItems.TEST_BREAD.toStack())
			.unlockedBy("has_item", has(Items.BREAD))
			.requires(Items.BREAD, 5)
			.save(recipeOutput, modLocation("special", "test"))

		SpecialRecipeBuilder.special { BreadSlicingRecipe() }
			.save(recipeOutput, modLocation("special", "crafting", "bread_slicing"))

		// Toaster
		FluidEnergyBuilder(
			::ToasterRecipe,
			listOf(ModItems.TOASTED_BREAD.get() to 2)
		)
			.itemRequired(Items.BREAD, 2)
			.timeRequired(100)
			.save(recipeOutput, modLocation("toaster", "bread_to_toasted_bread"))

		FluidEnergyBuilder(
			::ToasterRecipe,
			listOf(ModItems.TOAST_SLICE.get() to 2)
		)
			.itemRequired(ModItems.BREAD_SLICE.get(), 2)
			.timeRequired(100)
			.save(recipeOutput, modLocation("toaster", "slice_to_toast"))

		FluidEnergyBuilder(
			::ToasterRecipe,
			listOf(Items.CHARCOAL to 2)
		)
			.itemRequired(ModItems.TOASTED_BREAD.get(), 2)
			.timeRequired(100)
			.save(recipeOutput, modLocation("toaster", "toasted_bread_to_charcoal"))

		FluidEnergyBuilder(
			::ToasterRecipe,
			listOf(Items.CHARCOAL to 2)
		)
			.itemRequired(ModItems.TOAST_SLICE.get(), 2)
			.timeRequired(100)
			.save(recipeOutput, modLocation("toaster", "toast_slice_to_charcoal"))

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
}