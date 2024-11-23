package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.BreadMod
import org.bread_experts_group.breadmod.recipe.crafting.AbstractCuttingRecipe
import org.bread_experts_group.breadmod.recipe.crafting.BreadSlicingRecipe
import org.bread_experts_group.breadmod.recipe.dough_machine.DoughMachineRecipe
import org.bread_experts_group.breadmod.recipe.dough_machine.DoughMachineRecipeSerializer
import org.bread_experts_group.breadmod.recipe.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipeSerializer
import org.bread_experts_group.breadmod.recipe.fluid_energy.FluidEnergyRecipeSerializer
import java.util.function.Supplier

object ModRecipeSerializers {
    val RECIPE_SERIALIZER_REGISTRY: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(
        Registries.RECIPE_SERIALIZER, BreadMod.ID
    )

    val WHEAT_CRUSHING: Supplier<RecipeSerializer<WheatCrusherRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("wheat_crushing", ::WheatCrusherRecipeSerializer)

    val DOUGH_MACHINE: Supplier<RecipeSerializer<DoughMachineRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("dough_machine", ::DoughMachineRecipeSerializer)

    val FLUID_ENERGY: Supplier<RecipeSerializer<FluidEnergyRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("fluid_energy", ::FluidEnergyRecipeSerializer)

    val BREAD_SLICE: Supplier<SimpleCraftingRecipeSerializer<AbstractCuttingRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("bread_slice_crafting") { ->
            SimpleCraftingRecipeSerializer { BreadSlicingRecipe() }
        }

}