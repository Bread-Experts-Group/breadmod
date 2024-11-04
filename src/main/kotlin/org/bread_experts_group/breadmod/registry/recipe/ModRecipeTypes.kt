package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.util.registerType

object ModRecipeTypes {
    val RECIPE_TYPE_REGISTRY: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(Registries.RECIPE_TYPE, Breadmod.ID)

    val WHEAT_CRUSHING = RECIPE_TYPE_REGISTRY.registerType<WheatCrusherRecipe>("wheat_crushing")
}