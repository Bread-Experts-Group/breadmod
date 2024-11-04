package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.registries.DeferredRegister
import org.bread_experts_group.breadmod.Breadmod
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipe
import org.bread_experts_group.breadmod.recipe.wheat_crushing.WheatCrusherRecipeSerializer
import java.util.function.Supplier

object ModRecipeSerializers {
    val RECIPE_SERIALIZER_REGISTRY: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(
        Registries.RECIPE_SERIALIZER, Breadmod.ID
    )

    val WHEAT_CRUSHING: Supplier<RecipeSerializer<WheatCrusherRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("wheat_crushing", ::WheatCrusherRecipeSerializer)
}