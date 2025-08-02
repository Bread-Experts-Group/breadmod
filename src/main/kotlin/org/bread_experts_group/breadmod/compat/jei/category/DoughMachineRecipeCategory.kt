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
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.compat.jei.ModJEIRecipeTypes
import org.bread_experts_group.breadmod.compat.jei.createCachedArrow
import org.bread_experts_group.breadmod.compat.jei.drawEnergyTooltip
import org.bread_experts_group.breadmod.compat.jei.drawRecipeTime
import org.bread_experts_group.breadmod.compat.jei.drawableItemStack
import org.bread_experts_group.breadmod.compat.jei.getCachedArrow
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.itemStack

class DoughMachineRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<DoughMachineRecipe> {
	private val cachedArrows: LoadingCache<ULong, IDrawableAnimated> = createCachedArrow(
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
	override fun getIcon(): IDrawable = this.guiHelper.drawableItemStack(ModBlocks.DOUGH_MACHINE)
	override fun getWidth(): Int = 147
	override fun getHeight(): Int = 55

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DoughMachineRecipe, focuses: IFocusGroup) {
		builder.addSlot(RecipeIngredientRole.INPUT, 12, 28).addItemStack(
			recipe.rItemInputs.getOrNull(0)?.itemStack() ?: ItemStack.EMPTY
		)
		builder.addSlot(RecipeIngredientRole.INPUT, 47, 28).addItemStack(
			recipe.rItemInputs.getOrNull(1)?.itemStack() ?: ItemStack.EMPTY
		)
		if (recipe.rFluidInputs.isNotEmpty()) builder.addSlot(RecipeIngredientRole.INPUT, 123, 23)
			.addFluidStack(
				recipe.rFluidInputs.first().value,
				recipe.rFluidInputs.first().amount.toLong()
			).setFluidRenderer(10000, true, 16, 28)
			.addRichTooltipCallback { _, tooltip ->
				tooltip.add(Component.literal("Input").withStyle(ChatFormatting.ITALIC, ChatFormatting.BLUE))
			}
		if (recipe.rFluidOutputs.isNotEmpty()) builder.addSlot(RecipeIngredientRole.OUTPUT, 123, 4)
			.addFluidStack(
				recipe.rFluidOutputs.first().value,
				recipe.rFluidOutputs.first().amount.toLong()
			).setFluidRenderer(10000, true, 16, 16)
			.addRichTooltipCallback { _, tooltip ->
				tooltip.add(Component.literal("Output").withStyle(ChatFormatting.ITALIC, ChatFormatting.RED))
			}

		builder.addSlot(RecipeIngredientRole.OUTPUT, 78, 31).addItemStacks(
			recipe.rItemOutputs.map { it.itemStack() }
		)
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
		ModGuiElements.FLAT_BACKGROUND.blit(guiGraphics, 0, 0)
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