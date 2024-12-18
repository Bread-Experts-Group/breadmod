package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.test.FluidEnergyRecipeTest
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.single.SingleFluidItemRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.dough_machine.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe
import java.util.function.Supplier

object ModRecipeTypes {
    val RECIPE_TYPE_REGISTRY: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Registries.RECIPE_TYPE, BreadMod.ID)

    val WHEAT_CRUSHING: Supplier<RecipeType<WheatCrusherRecipe>> = registerRecipeType("wheat_crushing")
    val DOUGH_MACHINE: Supplier<RecipeType<DoughMachineRecipe>> = registerRecipeType("dough_machine")

    // exp

    val MULTI_ITEM: Supplier<RecipeType<MultiItemTestRecipe>> = registerRecipeType("multi_item_test")
    val MULTI_FLUID: Supplier<RecipeType<MultiFluidTestRecipe>> = registerRecipeType("multi_fluid_test")
    val SINGLE_ITEM: Supplier<RecipeType<SingleItemTestRecipe>> = registerRecipeType("single_item_test")
    val SINGLE_FLUID: Supplier<RecipeType<SingleFluidTestRecipe>> = registerRecipeType("single_fluid_test")
    val SINGLE_FLUID_ITEM: Supplier<RecipeType<SingleFluidItemRecipe>> = registerRecipeType("single_fluid_item_test")

    val FLUID_ENERGY_TEST: Supplier<RecipeType<FluidEnergyRecipeTest>> = registerRecipeType("fluid_energy_test")

    private fun <T : Recipe<*>> registerRecipeType(name: String): Supplier<RecipeType<T>> =
        RECIPE_TYPE_REGISTRY.register(name) { ->
            object : RecipeType<T> {
                override fun toString(): String = modLocation(name).toString()
            }
        }
}