package breadmod.compat.jei

import breadmod.BreadMod
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
import net.minecraft.world.item.Items


class BreadModCategory: IRecipeCategory<ArmorPotionRecipe> {
    private val armorPotionRecipeType: RecipeType<ArmorPotionRecipe> = RecipeType.create("breadmod", "bread_potion_crafting", ArmorPotionRecipe::class.java)
    private val width = 176
    private val height = 80
    private val guiThing: IGuiHelper = null!! // Probably terrible practice but it's just boilerplate

    override fun getRecipeType(): RecipeType<ArmorPotionRecipe> {
        return armorPotionRecipeType
    }

    override fun getTitle(): Component {
        return Component.translatable(BreadMod.modTranslatable("category", "bread_potion_crafting_test").toString())
    }

    override fun getBackground(): IDrawable {
        return guiThing.createBlankDrawable(width, height)
    }

    override fun getIcon(): IDrawable {
        return guiThing.createDrawableItemStack(Items.BREAD.defaultInstance)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ArmorPotionRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1)
            .addItemStack(ModItems.BREAD_CHESTPLATE.get().defaultInstance)
        builder.addSlot(RecipeIngredientRole.OUTPUT, 2, 2)
            .addItemStack(ModItems.BREAD_CHESTPLATE.get().defaultInstance)
    }
}