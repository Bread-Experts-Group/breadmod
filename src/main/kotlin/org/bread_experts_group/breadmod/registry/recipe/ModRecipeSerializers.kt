package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipeMulti
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergySerializer
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidItemRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.AbstractCuttingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.MicrowaveRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.RecipeFunctionDataFixer
import java.util.function.Supplier

object ModRecipeSerializers {
	val RECIPE_SERIALIZER_REGISTRY: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(
		Registries.RECIPE_SERIALIZER, BreadMod.ID
	)

	private fun <T : FluidEnergyRecipeMulti> registerFERSupplier(
		name: String,
		recipe: RecipeFunctionDataFixer<T>
	): Supplier<RecipeSerializer<T>> =
		this.RECIPE_SERIALIZER_REGISTRY.register(name) { -> FluidEnergySerializer(recipe) }

	val WHEAT_CRUSHING: Supplier<RecipeSerializer<WheatCrusherRecipe>> =
		this.registerFERSupplier("wheat_crushing", ::WheatCrusherRecipe)
	val DOUGH_MACHINE: Supplier<RecipeSerializer<DoughMachineRecipe>> =
		this.registerFERSupplier("dough_machine", ::DoughMachineRecipe)
	val TOASTER: Supplier<RecipeSerializer<ToasterRecipe>> =
		this.registerFERSupplier("toasting", ::ToasterRecipe)
	val MICROWAVE: Supplier<RecipeSerializer<MicrowaveRecipe>> =
		this.registerFERSupplier("microwaving", ::MicrowaveRecipe)
	val BREAD_SLICE: Supplier<SimpleCraftingRecipeSerializer<AbstractCuttingRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("bread_slice_crafting") { ->
			SimpleCraftingRecipeSerializer { BreadSlicingRecipe() }
		}

	// Exp.
	val MULTI_ITEM_TEST: Supplier<RecipeSerializer<MultiItemTestRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("multi_item_test", MultiItemTestRecipe::Serializer)
	val SINGLE_ITEM_TEST: Supplier<RecipeSerializer<SingleItemTestRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("single_item_test", SingleItemTestRecipe::Serializer)
	val SINGLE_FLUID_TEST: Supplier<RecipeSerializer<SingleFluidTestRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("single_fluid_test", SingleFluidTestRecipe::Serializer)
	val MULTI_FLUID_TEST: Supplier<RecipeSerializer<MultiFluidTestRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("multi_fluid_test", MultiFluidTestRecipe::Serializer)
	val SINGLE_FLUID_ITEM: Supplier<RecipeSerializer<SingleFluidItemRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("single_fluid_item", SingleFluidItemRecipe::Serializer)
	val FLUID_ENERGY_TEST: Supplier<RecipeSerializer<FluidEnergyRecipeMulti>> =
		this.registerFERSupplier("fluid_energy", ::FluidEnergyRecipeTest)
}