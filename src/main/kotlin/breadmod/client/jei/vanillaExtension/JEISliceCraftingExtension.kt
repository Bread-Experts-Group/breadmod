package breadmod.client.jei.vanillaExtension

import breadmod.client.jei.recipeList
import breadmod.client.jei.itemTagToList
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Item

class JEISliceCraftingExtension(
    private val pInputItem: Item,
    private val pOutputItem: Item,
    private val pInputMultiplier: Int,
    private val pInputRepeatCount: Int,
    private val pOutputMultiplier: Int,
    private val pOutputRepeatCount: Int
): ICraftingCategoryExtension {
    override fun getWidth(): Int = 2
    override fun getHeight(): Int = 1
    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        craftingGridHelper: ICraftingGridHelper,
        focuses: IFocusGroup
    ) {
        craftingGridHelper.createAndSetInputs(
            builder,
            listOf(
                itemTagToList(ItemTags.SWORDS),
                recipeList(pInputItem, pInputMultiplier, pInputRepeatCount)
            ), width, height
        )
        craftingGridHelper.createAndSetOutputs(
            builder,
            recipeList(pOutputItem, pOutputMultiplier, pOutputRepeatCount)

        )
    }
}