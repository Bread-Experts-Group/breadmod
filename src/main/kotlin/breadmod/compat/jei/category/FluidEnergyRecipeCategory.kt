package breadmod.compat.jei.category

import breadmod.compat.jei.ModJEIRecipeTypes.fluidEnergyRecipeType
import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlocks
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableStatic
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory

abstract class FluidEnergyRecipeCategory(guiHelper: IGuiHelper): IRecipeCategory<FluidEnergyRecipe> {
    private val background: IDrawableStatic = guiHelper.createBlankDrawable(1,1)
    private val icon: IDrawable = guiHelper.createDrawableItemStack(ModBlocks.DOUGH_MACHINE_BLOCK.get().defaultInstance)

    override fun getRecipeType(): RecipeType<FluidEnergyRecipe> = fluidEnergyRecipeType
    override fun getBackground(): IDrawable = background
    override fun getIcon(): IDrawable = icon
}