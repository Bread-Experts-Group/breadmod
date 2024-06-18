package breadmodadvanced.registry.recipe

import breadmodadvanced.ModMainAdv
import breadmodadvanced.recipe.fluidEnergy.generators.DieselGeneratorRecipe
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject

object ModRecipeTypesAdv {
    val deferredRegister: DeferredRegister<RecipeType<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModMainAdv.ID)

    private fun <T: Recipe<*>> register(name: String): RegistryObject<RecipeType<T>> =
        deferredRegister.register(name) { object: RecipeType<T> { override fun toString(): String = name } }

    val DIESEL_GENERATOR = register<DieselGeneratorRecipe>("diesel_generator")
}