package breadmod.registry.recipe

import breadmod.ModMain
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.recipe.fluidEnergy.generators.CoalGeneratorRecipe
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeTypes {
    val deferredRegister: DeferredRegister<RecipeType<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModMain.ID)

    private fun <T: Recipe<*>> register(name: String): RecipeType<T> =
        object : RecipeType<T> { override fun toString(): String = name }

    val DOUGH_MACHINE = register<DoughMachineRecipe>("dough_machine")
    val WHEAT_CRUSHING = register<WheatCrushingRecipe>("wheat_crusher")

    // Power Generators
    val COAL_GENERATOR = register<CoalGeneratorRecipe>("coal_generator")
}