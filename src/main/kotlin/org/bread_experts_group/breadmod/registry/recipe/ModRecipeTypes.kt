package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy_recipe.test.FluidEnergyRecipeTest
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidItemRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.MicrowaveRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import java.util.function.Supplier

object ModRecipeTypes {
	val RECIPE_TYPE_REGISTRY : DeferredRegister<RecipeType<*>> =
		DeferredRegister.create(Registries.RECIPE_TYPE, BreadMod.ID)
	val WHEAT_CRUSHING : Supplier<RecipeType<WheatCrusherRecipe>> = this.registerRecipeType("wheat_crushing")
	val DOUGH_MACHINE : Supplier<RecipeType<DoughMachineRecipe>> = this.registerRecipeType("dough_machine")
	val TOASTING : Supplier<RecipeType<ToasterRecipe>> = this.registerRecipeType("toasting")
	val MICROWAVE : Supplier<RecipeType<MicrowaveRecipe>> = this.registerRecipeType("microwaving")

	// exp
	val MULTI_ITEM : Supplier<RecipeType<MultiItemTestRecipe>> = this.registerRecipeType("multi_item_test")
	val MULTI_FLUID : Supplier<RecipeType<MultiFluidTestRecipe>> = this.registerRecipeType("multi_fluid_test")
	val SINGLE_ITEM : Supplier<RecipeType<SingleItemTestRecipe>> = this.registerRecipeType("single_item_test")
	val SINGLE_FLUID : Supplier<RecipeType<SingleFluidTestRecipe>> = this.registerRecipeType("single_fluid_test")
	val SINGLE_FLUID_ITEM : Supplier<RecipeType<SingleFluidItemRecipe>> =
		this.registerRecipeType("single_fluid_item_test")
	val FLUID_ENERGY_TEST : Supplier<RecipeType<FluidEnergyRecipeTest>> = this.registerRecipeType("fluid_energy_test")
	private fun <T : Recipe<*>> registerRecipeType(name : String) : Supplier<RecipeType<T>> =
		this.RECIPE_TYPE_REGISTRY.register(name) { ->
			object : RecipeType<T> {
				override fun toString() : String = modLocation(name).toString()
			}
		}
}