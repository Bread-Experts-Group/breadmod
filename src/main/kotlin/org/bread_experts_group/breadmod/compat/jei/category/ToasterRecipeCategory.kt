package org.bread_experts_group.breadmod.compat.jei.category

import com.google.common.cache.LoadingCache
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole.INPUT
import mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT
import mezz.jei.api.recipe.RecipeIngredientRole.RENDER_ONLY
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.compat.jei.ModJEIRecipeTypes
import org.bread_experts_group.breadmod.compat.jei.buildBackground
import org.bread_experts_group.breadmod.compat.jei.createCachedArrow
import org.bread_experts_group.breadmod.compat.jei.drawRecipeTime
import org.bread_experts_group.breadmod.compat.jei.drawableItemStack
import org.bread_experts_group.breadmod.compat.jei.getCachedArrow
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe

// todo replace drawing with GuiElements
class ToasterRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<ToasterRecipe> {
	private val cachedArrows: LoadingCache<Int, IDrawableAnimated> = createCachedArrow(
		this.guiHelper,
		22,
		ModGuiElements.TOASTER_ARROW_FILLED_JEI.actualLocation(true),
		0,
		0,
		29,
		22,
		IDrawableAnimated.StartDirection.TOP
	)

	override fun getRecipeType(): RecipeType<ToasterRecipe> = ModJEIRecipeTypes.TOASTER_RECIPE_TYPE
	override fun getTitle(): Component = Component.translatable(ModBlocks.TOASTER.get().descriptionId)
	override fun getBackground(): IDrawable = this.guiHelper.buildBackground(66, 66)
	override fun getIcon(): IDrawable = this.guiHelper.drawableItemStack(ModBlocks.TOASTER)

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ToasterRecipe, focuses: IFocusGroup) {
		builder.addSlot(RENDER_ONLY, 9, 46).addItemStack(ModBlocks.TOASTER.toStack())
		builder.addSlot(INPUT, 9, 4).addItemStacks(recipe.getInputItems())

		recipe.rItemOutputs.let(builder.addSlot(OUTPUT, 41, 34)::addItemStacks)
	}

	override fun draw(
		recipe: ToasterRecipe,
		recipeSlotsView: IRecipeSlotsView,
		guiGraphics: GuiGraphics,
		mouseX: Double,
		mouseY: Double
	) {
		val arrow = getCachedArrow(recipe, this.cachedArrows)
		ModGuiElements.TOASTER_ARROW_JEI.blit(guiGraphics, 27, 10)
		arrow.draw(guiGraphics, 27, 10)

		ModGuiElements.SLOT.blit(guiGraphics, 8, 3)
		ModGuiElements.SLOT.blit(guiGraphics, 40, 33)
		ModGuiElements.FLAME.blit(guiGraphics, 10, 26)
		ModGuiElements.BACKGROUND_ALT.blit(guiGraphics, 7, 44)
		drawRecipeTime(recipe, guiGraphics, 40, 52)
	}
}