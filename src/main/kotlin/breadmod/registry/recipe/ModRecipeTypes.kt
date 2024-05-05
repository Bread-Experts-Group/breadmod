package breadmod.registry.recipe

import breadmod.BreadMod
import breadmod.recipe.AbstractFluidEnergyRecipe
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeTypes {
    val deferredRegister: DeferredRegister<RecipeType<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, BreadMod.ID)

    private fun <T: Recipe<*>> register(name: String): RecipeType<T> =
        object : RecipeType<T> { override fun toString(): String = name }

    val ENERGY_FLUID_ITEM = register<AbstractFluidEnergyRecipe>("energy_fluid_item")
}