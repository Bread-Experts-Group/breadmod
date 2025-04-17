package org.bread_experts_group.breadmod.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.data.recipes.SpecialRecipeBuilder
import net.minecraft.tags.FluidTags
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyBuilder
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidItemRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import java.util.concurrent.CompletableFuture

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
		// Exp
		MultiItemTestRecipe.Builder(listOf(Items.BREAD to 5))
			.itemRequired(ModItems.FLOUR.get(), 3)
			.itemRequired(ItemTags.BEDS)
			.timeRequired(100)
			.save(recipeOutput, modLocation("experimental", "multi_item_test"))
		MultiItemTestRecipe.Builder(listOf(ModItems.TOOL_GUN.get() to 1))
			.itemRequired(ModItems.TOASTER_HEATING_ELEMENT.get())
			.itemRequired(ItemTags.HOES)
			.itemRequired(ItemTags.ANVIL, 3)
			.timeRequired(50)
			.save(recipeOutput, modLocation("experimental", "multi_item_test_two"))

		MultiFluidTestRecipe.Builder(listOf(Fluids.WATER to 1000, Fluids.LAVA to 500))
			.fluidRequired(ModFluids.BREAD_LIQUID.source.get())
			.fluidRequired(FluidTags.LAVA, 250)
			.timeRequired(100)
			.save(recipeOutput, modLocation("experimental", "multi_fluid_test"))

		SingleItemTestRecipe.Builder(ModItems.FLOUR.get(), 10)
			.itemRequired(Items.BREAD, 5)
			.timeRequired(50)
			.save(recipeOutput, modLocation("experimental", "single_item_test"))
		SingleItemTestRecipe.Builder(Items.COD, 1)
			.itemRequired(Items.PUFFERFISH)
			.timeRequired(100)
			.save(recipeOutput, modLocation("experimental", "single_item_test_two"))

		SingleFluidTestRecipe.Builder(ModFluids.BREAD_LIQUID.source.get(), 1000)
			.fluidRequired(Fluids.WATER, 500)
			.timeRequired(100)
			.save(recipeOutput, modLocation("experimental", "single_fluid_test"))

		SingleFluidItemRecipe.Builder(Items.COD to 4, Fluids.LAVA to 500)
			.itemRequired(Items.BREAD, 2)
			.fluidRequired(Fluids.WATER, 500)
			.timeRequired(100)
			.save(recipeOutput, modLocation("experimental", "single_fluid_item_test"))
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
	}
}