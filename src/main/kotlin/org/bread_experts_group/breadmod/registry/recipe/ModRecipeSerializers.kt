package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.MicrowaveRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.AbstractCuttingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.ToastSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipeMulti
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergySerializer
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.RecipeFunctionDataFixer
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
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
	val TOAST_SLICE: Supplier<SimpleCraftingRecipeSerializer<AbstractCuttingRecipe>> =
		this.RECIPE_SERIALIZER_REGISTRY.register("toast_slice_crafting") { ->
			SimpleCraftingRecipeSerializer { ToastSlicingRecipe() }
		}

	// Exp.
	val FLUID_ENERGY_TEST: Supplier<RecipeSerializer<FluidEnergyRecipeMulti>> =
		this.registerFERSupplier("fluid_energy", ::FluidEnergyRecipeTest)
}