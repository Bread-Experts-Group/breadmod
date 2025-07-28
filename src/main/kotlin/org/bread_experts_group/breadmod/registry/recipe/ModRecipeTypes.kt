package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.RegistryProvider
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.MicrowaveRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test.FluidEnergyRecipeTest
import java.util.function.Supplier

object ModRecipeTypes : RegistryProvider(Registries.RECIPE_TYPE) {
	private val registry: DeferredRegister<RecipeType<*>> = this.getRegistry(Registries.RECIPE_TYPE)
	val WHEAT_CRUSHING: Supplier<RecipeType<WheatCrusherRecipe>> = this.registerRecipeType("wheat_crushing")
	val DOUGH_MACHINE: Supplier<RecipeType<DoughMachineRecipe>> = this.registerRecipeType("dough_machine")
	val TOASTING: Supplier<RecipeType<ToasterRecipe>> = this.registerRecipeType("toasting")
	val MICROWAVE: Supplier<RecipeType<MicrowaveRecipe>> = this.registerRecipeType("microwaving")

	// exp
	val FLUID_ENERGY_TEST: Supplier<RecipeType<FluidEnergyRecipeTest>> = this.registerRecipeType("fluid_energy_test")
	private fun <T : Recipe<*>> registerRecipeType(name: String): Supplier<RecipeType<T>> =
		this.registry.register(name) { ->
			object : RecipeType<T> {
				override fun toString(): String = modLocation(name).toString()
			}
		}
}