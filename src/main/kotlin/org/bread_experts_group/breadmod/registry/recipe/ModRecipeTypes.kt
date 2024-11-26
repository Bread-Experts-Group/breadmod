package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.registry.recipe.actual.dough_machine.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.experimental.multi.MultiItemTestRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.util.registerType

object ModRecipeTypes {
    val RECIPE_TYPE_REGISTRY: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Registries.RECIPE_TYPE, BreadMod.ID)

    val WHEAT_CRUSHING = RECIPE_TYPE_REGISTRY.registerType<WheatCrusherRecipe>("wheat_crushing")
    val DOUGH_MACHINE = RECIPE_TYPE_REGISTRY.registerType<DoughMachineRecipe>("dough_machine")

    val FLUID_ENERGY = RECIPE_TYPE_REGISTRY.registerType<FluidEnergyRecipe>("fluid_energy")

    // exp

    val MULTI_ITEM = RECIPE_TYPE_REGISTRY.registerType<MultiItemTestRecipe>("multi_item_test")
}