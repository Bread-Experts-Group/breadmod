package breadmod.compat.jei.category

import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlocks
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import net.minecraft.network.chat.Component
import net.minecraft.world.item.crafting.Ingredient

class DoughMachineRecipeCategory(guiHelper: IGuiHelper): FluidEnergyRecipeCategory(guiHelper) {
    override fun getTitle(): Component = Component.translatable(ModBlocks.DOUGH_MACHINE_BLOCK.get().descriptionId)

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: FluidEnergyRecipe, focuses: IFocusGroup) {
        builder.setShapeless()
        builder.addSlot(RecipeIngredientRole.INPUT, 0, 0).let {
            it.addIngredients(recipe.itemsRequiredTagged?.firstOrNull().let { if(it != null) Ingredient.of(it.first) else null }
                ?: Ingredient.of(recipe.itemsRequired!!.first()))
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 10, 0).let {
            recipe.itemsOutput?.firstOrNull()?.let { item -> it.addItemStack(item) }
        }
    }
}