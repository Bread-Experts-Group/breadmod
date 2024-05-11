package breadmod.compat.jei

import breadmod.ModMain
import breadmod.recipe.ArmorPotionRecipe
import breadmod.registry.item.ModItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component
import vectorwing.farmersdelight.common.utility.RecipeUtils

class TestRecipeCategory(guiHelper: IGuiHelper): IRecipeCategory<ArmorPotionRecipe> {
    private val background = guiHelper.createBlankDrawable(1,1)
    private val icon = guiHelper.createDrawableItemStack(ModItems.BREAD_CHESTPLATE.get().defaultInstance)

    override fun getRecipeType(): RecipeType<ArmorPotionRecipe> {
        return potionRecipe
    }

    override fun getTitle(): Component {
        return Component.literal("Armor Potion Recipe")
    }

    override fun getBackground(): IDrawable {
        return background
    }

    override fun getIcon(): IDrawable {
        return icon
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ArmorPotionRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.ingredients[0])
        builder.addSlot(RecipeIngredientRole.OUTPUT, 2, 2).addItemStack(RecipeUtils.getResultItem(recipe))
    }

    companion object Testing {
        val potionRecipe: RecipeType<ArmorPotionRecipe> = RecipeType.create(ModMain.ID, "bread_potion", ArmorPotionRecipe::class.java)
    }
}