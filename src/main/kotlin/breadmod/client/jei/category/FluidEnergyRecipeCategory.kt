package breadmod.client.jei.category


import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import breadmod.registry.block.ModBlocks
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableStatic
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.category.IRecipeCategory

abstract class FluidEnergyRecipeCategory<T: FluidEnergyRecipe>(guiHelper: IGuiHelper): IRecipeCategory<T> {
    private val background: IDrawableStatic = guiHelper.createBlankDrawable(1,1)
    private val icon: IDrawable = guiHelper.createDrawableItemStack(ModBlocks.DOUGH_MACHINE_BLOCK.get().defaultInstance)

    override fun getBackground(): IDrawable = background
    override fun getIcon(): IDrawable = icon
}