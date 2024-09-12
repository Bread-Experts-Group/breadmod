package bread.mod.breadmod.registry.recipe

import bread.mod.breadmod.ModMainCommon.MOD_ID
import bread.mod.breadmod.recipe.BreadSliceRecipe
import bread.mod.breadmod.recipe.toaster.ToasterRecipe
import bread.mod.breadmod.recipe.toaster.ToasterRecipeSerializer
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer

object ModRecipeSerializers {
    internal val RECIPE_SERIALIZER_REGISTRY: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(
        MOD_ID, Registries.RECIPE_SERIALIZER
    )

    val BREAD_SLICE: RegistrySupplier<SimpleCraftingRecipeSerializer<BreadSliceRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("bread_slice_crafting") {
            SimpleCraftingRecipeSerializer { BreadSliceRecipe() }
        }

    val TOASTING: RegistrySupplier<RecipeSerializer<ToasterRecipe>> =
        RECIPE_SERIALIZER_REGISTRY.register("toasting") { ToasterRecipeSerializer() }
}