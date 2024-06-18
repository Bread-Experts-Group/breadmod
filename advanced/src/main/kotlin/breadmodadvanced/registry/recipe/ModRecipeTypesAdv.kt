package breadmodadvanced.registry.recipe

import breadmod.registry.recipe.ModRecipeTypes.registerType
import breadmodadvanced.ModMainAdv
import breadmodadvanced.recipe.fluidEnergy.generators.DieselGeneratorRecipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeTypesAdv {
    internal val deferredRegister: DeferredRegister<RecipeType<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModMainAdv.ID)
    val DIESEL_GENERATOR = deferredRegister.registerType<DieselGeneratorRecipe>("diesel_generator")
}