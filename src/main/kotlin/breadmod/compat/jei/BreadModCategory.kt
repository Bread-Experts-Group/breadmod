package breadmod.compat.jei

import breadmod.BreadMod
import breadmod.recipe.ArmorPotionRecipe
import breadmod.registry.block.ModBlocks
import breadmod.registry.item.ModItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.network.chat.Component


class BreadModCategory(guiHelper: IGuiHelper): IRecipeCategory<ArmorPotionRecipe> {
    private val armorPotionRecipeType: RecipeType<ArmorPotionRecipe> = RecipeType.create(BreadMod.ID, "bread_potion_crafting", ArmorPotionRecipe::class.java)
    private val width = 176
    private val height = 80
    private val background = guiHelper.createBlankDrawable(width, height)
    private val icon = guiHelper.createDrawableItemStack(ModBlocks.BREAD_BLOCK.get().defaultInstance)

    override fun getRecipeType(): RecipeType<ArmorPotionRecipe> = armorPotionRecipeType
    override fun getTitle(): Component = BreadMod.modTranslatable("category", "bread_potion_crafting_test")
    override fun getBackground(): IDrawable = background
    override fun getIcon(): IDrawable = icon

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ArmorPotionRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
            .addItemStack(ModItems.BREAD_CHESTPLATE.get().defaultInstance)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 2, 2)
            .addItemStack(ModItems.BREAD_CHESTPLATE.get().defaultInstance)
    }
}