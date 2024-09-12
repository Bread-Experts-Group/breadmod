package bread.mod.breadmod.registry.recipe

import bread.mod.breadmod.ModMainCommon.MOD_ID
import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.recipe.toaster.ToasterRecipe
import bread.mod.breadmod.util.registerType
import dev.architectury.registry.registries.DeferredRegister
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.RecipeType

object ModRecipeTypes {
    val RECIPE_TYPE_REGISTRY: DeferredRegister<RecipeType<*>> = DeferredRegister.create(MOD_ID, Registries.RECIPE_TYPE)

    val TOASTING = RECIPE_TYPE_REGISTRY.registerType<ToasterRecipe>(modLocation("toasting"))
}