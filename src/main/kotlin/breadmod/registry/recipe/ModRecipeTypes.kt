package breadmod.registry.recipe

import breadmod.ModMain
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.recipe.fluidEnergy.ToasterRecipe
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.util.registerType
import net.minecraft.world.item.crafting.RecipeType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries

object ModRecipeTypes {
    internal val deferredRegister: DeferredRegister<RecipeType<*>> =
        DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModMain.ID)

    val DOUGH_MACHINE = deferredRegister.registerType<DoughMachineRecipe>("dough_machine")
    val WHEAT_CRUSHING = deferredRegister.registerType<WheatCrushingRecipe>("wheat_crusher")
    val TOASTING = deferredRegister.registerType<ToasterRecipe>("toasting")
}