package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.MicrowaveRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.AbstractCuttingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadArmorPotionRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.DopedBreadRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.crafting.ToastSlicingRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergySerializer
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.RecipeFunctionDataFixer
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import java.util.Optional
import java.util.function.Supplier
import kotlin.jvm.optionals.getOrNull

object ModRecipeSerializers : RegistryProvider(Registries.RECIPE_SERIALIZER) {
	private val registry: DeferredRegister<RecipeSerializer<*>> = this.getRegistry(Registries.RECIPE_SERIALIZER)
	private fun <R : FluidEnergyRecipe> registerFERSupplier(
		name: String,
		recipe: RecipeFunctionDataFixer<R>
	): Supplier<RecipeSerializer<R>> = this.registry.register(name) { -> FluidEnergySerializer(recipe) }

	private fun <T> Optional<List<T>>.unwrap(): List<T> = this.orElse(emptyList())
	val WHEAT_CRUSHING: Supplier<RecipeSerializer<WheatCrusherRecipe>> =
		this.registerFERSupplier("wheat_crushing") { ii, io, _, _, time, energy ->
			WheatCrusherRecipe(ii.unwrap(), io.unwrap(), time, energy.getOrNull())
		}
	val DOUGH_MACHINE: Supplier<RecipeSerializer<DoughMachineRecipe>> =
		this.registerFERSupplier("dough_machine") { ii, io, fi, fo, time, energy ->
			DoughMachineRecipe(
				ii.unwrap(), io.unwrap(), fi.unwrap(), fo.unwrap(),
				time, energy.getOrNull()
			)
		}
	val TOASTER: Supplier<RecipeSerializer<ToasterRecipe>> =
		this.registerFERSupplier("toasting") { ii, io, _, _, time, _ ->
			ToasterRecipe(ii.unwrap(), io.unwrap(), time)
		}
	val MICROWAVE: Supplier<RecipeSerializer<MicrowaveRecipe>> =
		this.registerFERSupplier("microwaving") { ii, io, _, _, time, _ ->
			MicrowaveRecipe(ii.unwrap(), io.unwrap(), time)
		}
	val BREAD_SLICE: Supplier<SimpleCraftingRecipeSerializer<AbstractCuttingRecipe>> =
		this.registry.register("bread_slice_crafting") { ->
			SimpleCraftingRecipeSerializer { BreadSlicingRecipe() }
		}
	val TOAST_SLICE: Supplier<SimpleCraftingRecipeSerializer<AbstractCuttingRecipe>> =
		this.registry.register("toast_slice_crafting") { ->
			SimpleCraftingRecipeSerializer { ToastSlicingRecipe() }
		}
	val ARMOR_POTION: Supplier<SimpleCraftingRecipeSerializer<BreadArmorPotionRecipe>> =
		this.registry.register("bread_armor_potion_crafting") { ->
			SimpleCraftingRecipeSerializer { BreadArmorPotionRecipe() }
		}
	val BREAD_DOPING: Supplier<SimpleCraftingRecipeSerializer<DopedBreadRecipe>> =
		this.registry.register("doped_bread_crafting") { ->
			SimpleCraftingRecipeSerializer { DopedBreadRecipe() }
		}

	// Exp.
	val FLUID_ENERGY_TEST: Supplier<RecipeSerializer<FluidEnergyRecipe>> =
		this.registerFERSupplier("fluid_energy") { ii, io, fi, fo, time, energy ->
			FluidEnergyRecipeTest(
				ii.unwrap(), io.unwrap(), fi.unwrap(), fo.unwrap(),
				time, energy.getOrNull()
			)
		}
}