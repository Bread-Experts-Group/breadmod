package org.bread_experts_group.breadmod.compat.jei.category

import com.google.common.cache.LoadingCache
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.builder.ITooltipBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.compat.jei.ModJEIRecipeTypes
import org.bread_experts_group.breadmod.compat.jei.createCachedArrow
import org.bread_experts_group.breadmod.compat.jei.drawEnergyTooltip
import org.bread_experts_group.breadmod.compat.jei.drawRecipeTime
import org.bread_experts_group.breadmod.compat.jei.drawRotatedArrow
import org.bread_experts_group.breadmod.compat.jei.drawableItemStack
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.itemStack

class WheatCrusherRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<WheatCrusherRecipe> {
	private val cachedArrow: LoadingCache<ULong, IDrawableAnimated> = createCachedArrow(
		this.guiHelper,
		48,
		ModGuiElements.WHEAT_CRUSHER_ARROW_FILLED.actualLocation(true),
		0,
		0,
		9,
		48,
		IDrawableAnimated.StartDirection.TOP
	)

	override fun getRecipeType(): RecipeType<WheatCrusherRecipe> = ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE
	override fun getTitle(): Component = Component.translatable(ModBlocks.WHEAT_CRUSHER.get().descriptionId)
	override fun getIcon(): IDrawable = this.guiHelper.drawableItemStack(ModBlocks.WHEAT_CRUSHER)
	override fun getWidth(): Int = 161
	override fun getHeight(): Int = 65

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: WheatCrusherRecipe, focuses: IFocusGroup) {
		builder.addSlot(RecipeIngredientRole.INPUT, 43, 24).addItemStacks(
			recipe.rItemInputs.map { it.itemStack() }
		)
		builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 24).addItemStacks(
			recipe.rItemOutputs.map { it.itemStack() }
		)
	}

	override fun getTooltip(
		tooltip: ITooltipBuilder,
		recipe: WheatCrusherRecipe,
		recipeSlotsView: IRecipeSlotsView,
		mouseX: Double,
		mouseY: Double
	) {
		if (ModGuiElements.ENERGY_METER.isMouseOver(mouseX, mouseY, 142, 9))
			tooltip.add(drawEnergyTooltip(recipe))
	}

	override fun draw(
		recipe: WheatCrusherRecipe,
		recipeSlotsView: IRecipeSlotsView,
		guiGraphics: GuiGraphics,
		mouseX: Double,
		mouseY: Double
	) {
		ModGuiElements.FLAT_BACKGROUND.blit(guiGraphics, 0, 0)
		ModGuiElements.WHEAT_CRUSHER_ARROW.setRotation(90f).blit(guiGraphics, 61, 36)
		drawRotatedArrow(guiGraphics, recipe, this.cachedArrow, 61, 36, 90f)
		drawRecipeTime(recipe, guiGraphics, 110, 46)

		ModGuiElements.WHEAT_CRUSHER_LEFT_WHEEL.blit(guiGraphics, 6, 16)
		ModGuiElements.RESULT_SLOT.blit(guiGraphics, 110, 19)
		ModGuiElements.SLOT.blitScaled(guiGraphics, 141, 8, 18, 49)
		ModGuiElements.ENERGY_METER.blit(guiGraphics, 142, 9)
		ModGuiElements.SLOT.blit(guiGraphics, 42, 23)
	}
}