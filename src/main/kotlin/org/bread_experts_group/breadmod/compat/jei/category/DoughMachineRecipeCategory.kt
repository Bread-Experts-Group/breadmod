package org.bread_experts_group.breadmod.compat.jei.category

import com.google.common.cache.LoadingCache
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.builder.ITooltipBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole.INPUT
import mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.ChatFormatting.BLUE
import net.minecraft.ChatFormatting.ITALIC
import net.minecraft.ChatFormatting.RED
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.compat.jei.ModJEIRecipeTypes
import org.bread_experts_group.breadmod.compat.jei.buildBackground
import org.bread_experts_group.breadmod.compat.jei.createCachedArrow
import org.bread_experts_group.breadmod.compat.jei.drawEnergyTooltip
import org.bread_experts_group.breadmod.compat.jei.drawRecipeTime
import org.bread_experts_group.breadmod.compat.jei.drawableItemStack
import org.bread_experts_group.breadmod.compat.jei.getCachedArrow
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe

class DoughMachineRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<DoughMachineRecipe> {
	private val cachedArrows: LoadingCache<Int, IDrawableAnimated> = createCachedArrow(
		this.guiHelper,
		21,
		ModGuiElements.DOUGH_MACHINE_ARROW_FILLED_JEI.actualLocation(true),
		0,
		0,
		76,
		21,
		IDrawableAnimated.StartDirection.LEFT
	)

	override fun getRecipeType(): RecipeType<DoughMachineRecipe> = ModJEIRecipeTypes.DOUGH_MACHINE_RECIPE_TYPE
	override fun getTitle(): Component = Component.translatable(ModBlocks.DOUGH_MACHINE.get().descriptionId)
	override fun getBackground(): IDrawable = this.guiHelper.buildBackground(147, 55)
	override fun getIcon(): IDrawable = this.guiHelper.drawableItemStack(ModBlocks.DOUGH_MACHINE)

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DoughMachineRecipe, focuses: IFocusGroup) {
		builder.addSlot(INPUT, 12, 28).addItemStacks(recipe.getInputItemsForIndex(0))
		builder.addSlot(INPUT, 47, 28).addItemStacks(recipe.getInputItemsForIndex(1))
		builder.addSlot(INPUT, 123, 23)
			.addFluidStack(
				recipe.getInputFluids().first().fluid,
				recipe.getInputFluids().first().amount.toLong()
			).setFluidRenderer(10000, true, 16, 28)
			.addRichTooltipCallback { _, tooltip ->
				tooltip.add(Component.literal("Input").withStyle(ITALIC, BLUE))
			}
		builder.addSlot(OUTPUT, 123, 4)
			.addFluidStack(
				recipe.rFluidOutputs.first().fluid,
				recipe.rFluidOutputs.first().amount.toLong()
			).setFluidRenderer(10000, true, 16, 16)
			.addRichTooltipCallback { _, tooltip ->
				tooltip.add(Component.literal("Output").withStyle(ITALIC, RED))
			}

		recipe.rItemOutputs.let(builder.addSlot(OUTPUT, 78, 31)::addItemStacks)
	}

	override fun getTooltip(
		tooltip: ITooltipBuilder,
		recipe: DoughMachineRecipe,
		recipeSlotsView: IRecipeSlotsView,
		mouseX: Double,
		mouseY: Double
	) {
		if (ModGuiElements.ENERGY_METER.isMouseOver(mouseX, mouseY, 101, 3))
			tooltip.add(drawEnergyTooltip(recipe))
	}

	override fun draw(
		recipe: DoughMachineRecipe,
		recipeSlotsView: IRecipeSlotsView,
		guiGraphics: GuiGraphics,
		mouseX: Double,
		mouseY: Double
	) {
		val arrow = getCachedArrow(recipe, this.cachedArrows)
		ModGuiElements.DOUGH_MACHINE_ARROW_JEI.blit(guiGraphics, 17, 5)
		arrow.draw(guiGraphics, 17, 5)
		drawRecipeTime(recipe, guiGraphics, 46, 46)

		ModGuiElements.SLOT.blit(guiGraphics, 11, 27)
		ModGuiElements.PLUS.blit(guiGraphics, 31, 29)
		ModGuiElements.SLOT.blitScaled(guiGraphics, 101, 3, 18, 49)
		ModGuiElements.ENERGY_METER.blit(guiGraphics, 102, 4)
		ModGuiElements.SLOT.blit(guiGraphics, 46, 27)
		ModGuiElements.RESULT_SLOT.blit(guiGraphics, 73, 26)

		ModGuiElements.SLOT.blitScaled(guiGraphics, 122, 22, 18, 30)
		ModGuiElements.SLOT.blit(guiGraphics, 122, 3)
	}
}