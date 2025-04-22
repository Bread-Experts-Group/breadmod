package org.bread_experts_group.breadmod.compat.jei.category

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole.INPUT
import mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.compat.jei.ModJEIRecipeTypes
import org.bread_experts_group.breadmod.compat.jei.createCachedArrows
import org.bread_experts_group.breadmod.compat.jei.drawArrow
import org.bread_experts_group.breadmod.compat.jei.drawRecipeTime
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.recipe.actual.WheatCrusherRecipe

class WheatCrusherRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<WheatCrusherRecipe> {
	private val texture = modLocation("textures", "gui", "jei", "gui_wheat_crusher.png")
	private val cachedArrows = createCachedArrows(
		this.guiHelper,
		48,
		this.texture,
		193,
		0,
		48,
		9,
		IDrawableAnimated.StartDirection.LEFT,
		false
	)

	override fun getRecipeType(): RecipeType<WheatCrusherRecipe> = ModJEIRecipeTypes.WHEAT_CRUSHER_RECIPE_TYPE
	override fun getTitle(): Component = Component.translatable(ModBlocks.WHEAT_CRUSHER.get().descriptionId)
	override fun getBackground(): IDrawable = this.guiHelper.createDrawable(this.texture, 0, 0, 161, 65)
	override fun getIcon(): IDrawable = this.guiHelper.createDrawableItemStack(ModBlocks.WHEAT_CRUSHER.toStack())

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: WheatCrusherRecipe, focuses: IFocusGroup) {
		builder.addSlot(INPUT, 43, 24)
			.addItemStacks(buildList { recipe.rItemInputs.forEach { it.items.forEach(this::add) } })

		recipe.rItemOutputs.let(builder.addSlot(OUTPUT, 115, 24)::addItemStacks)
	}

	private var step: Int = -32
	private var lastTick = 0
	override fun draw(
		recipe: WheatCrusherRecipe,
		recipeSlotsView: IRecipeSlotsView,
		guiGraphics: GuiGraphics,
		mouseX: Double,
		mouseY: Double
	) {
		val guiTicks = localClient.gui.guiTicks
		val arrow = drawArrow(recipe, this.cachedArrows)
		arrow.draw(guiGraphics, 61, 27)
		drawRecipeTime(recipe, guiGraphics, 110, 46)
		guiGraphics.blit(this.texture, 142, 9, 193, 9, 16, 47)

		guiGraphics.blit(this.texture, 6, 16, 161, this.step, 32, 32)
		if (this.lastTick <= guiTicks) {
			this.lastTick = guiTicks + 8
			if (this.step < 32) this.step += 32 else this.step = -32
		}
	}
}