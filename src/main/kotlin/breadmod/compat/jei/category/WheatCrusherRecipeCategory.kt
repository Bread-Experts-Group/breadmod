package breadmod.compat.jei.category

import breadmod.recipe.WheatCrusherRecipe
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component

class WheatCrusherRecipeCategory: IRecipeCategory<WheatCrusherRecipe> {
    override fun getRecipeType(): RecipeType<WheatCrusherRecipe> {
        TODO("Not yet implemented")
    }

    override fun getTitle(): Component {
        TODO("Not yet implemented")
    }

    override fun getBackground(): IDrawable {
        TODO("Not yet implemented")
    }

    override fun getIcon(): IDrawable {
        TODO("Not yet implemented")
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: WheatCrusherRecipe, focuses: IFocusGroup) {
        TODO("Not yet implemented")
    }
}