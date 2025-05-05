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
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.compat.jei.ModJEIRecipeTypes
import org.bread_experts_group.breadmod.compat.jei.createCachedArrows
import org.bread_experts_group.breadmod.compat.jei.drawArrow
import org.bread_experts_group.breadmod.compat.jei.drawRecipeTime
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.recipe.actual.ToasterRecipe

class ToasterRecipeCategory(private val guiHelper: IGuiHelper) : IRecipeCategory<ToasterRecipe> {
	private val texture: ResourceLocation = modLocation("textures", "gui", "jei", "gui_toaster.png")
	private val cachedArrows: LoadingCache<Int, IDrawableAnimated> = createCachedArrows(
		this.guiHelper,
		22,
		this.texture,
		66,
		0,
		29,
		22,
		IDrawableAnimated.StartDirection.TOP,
		false
	)

	override fun getRecipeType(): RecipeType<ToasterRecipe> = ModJEIRecipeTypes.TOASTER_RECIPE_TYPE
	override fun getTitle(): Component = Component.translatable(ModBlocks.TOASTER.get().descriptionId)
	override fun getBackground(): IDrawable = this.guiHelper.createDrawable(this.texture, 0, 0, 66, 66)
	override fun getIcon(): IDrawable = this.guiHelper.createDrawableItemStack(ModBlocks.TOASTER.get().defaultInstance)

	override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ToasterRecipe, focuses: IFocusGroup) {
		builder.addSlot(RENDER_ONLY, 9, 46).addItemStack(ModBlocks.TOASTER.toStack())
		builder.addSlot(INPUT, 9, 4)
			.addItemStacks(buildList { recipe.rItemInputs.forEach { it.items.forEach(this::add) } })

		recipe.rItemOutputs.let(builder.addSlot(OUTPUT, 41, 34)::addItemStacks)
	}

	override fun draw(
		recipe: ToasterRecipe,
		recipeSlotsView: IRecipeSlotsView,
		guiGraphics: GuiGraphics,
		mouseX: Double,
		mouseY: Double
	) {
		val arrow = drawArrow(recipe, this.cachedArrows)
		arrow.draw(guiGraphics, 27, 10)
		drawRecipeTime(recipe, guiGraphics, 40, 52)
	}
}