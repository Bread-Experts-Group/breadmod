package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.multi.MultiFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.single.SingleFluidTestRecipe
import org.bread_experts_group.breadmod.experimental.recipe_related.recipe.single.SingleItemTestRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.dough_machine.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.util.registerType
import java.util.function.Supplier

object ModRecipeTypes {
    val RECIPE_TYPE_REGISTRY: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Registries.RECIPE_TYPE, BreadMod.ID)

    val WHEAT_CRUSHING: Supplier<RecipeType<WheatCrusherRecipe>> = RECIPE_TYPE_REGISTRY.registerType("wheat_crushing")
    val DOUGH_MACHINE: Supplier<RecipeType<DoughMachineRecipe>> = RECIPE_TYPE_REGISTRY.registerType("dough_machine")

    // exp

    val MULTI_ITEM: Supplier<RecipeType<MultiItemTestRecipe>> = RECIPE_TYPE_REGISTRY.registerType("multi_item_test")
    val MULTI_FLUID: Supplier<RecipeType<MultiFluidTestRecipe>> = RECIPE_TYPE_REGISTRY.registerType("multi_fluid_test")
    val SINGLE_ITEM = RECIPE_TYPE_REGISTRY.registerType<SingleItemTestRecipe>("single_item_test")
    val SINGLE_FLUID = RECIPE_TYPE_REGISTRY.registerType<SingleFluidTestRecipe>("single_fluid_test")
}