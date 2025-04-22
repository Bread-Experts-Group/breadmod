package org.bread_experts_group.breadmod.compat.jei

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.helpers.IGuiHelper
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe

/**
 * Draws a recipe time string
 */
fun drawRecipeTime(recipe: FluidEnergyRecipe<*>, guiGraphics: GuiGraphics, x: Int, y: Int) {
	if (recipe.getTime() > 0) {
		val recipeTimeSeconds = recipe.getTime() / 20
		val timeString = modTranslatable("jei", "generic", "recipe_time", args = listOf("$recipeTimeSeconds"))
		guiGraphics.drawString(localClient.font, timeString, x, y, -8355712, false)
	}
}

/**
 * Builder for creating a progressive arrow sprite based on recipe time
 */
fun createCachedArrows(
	guiHelper: IGuiHelper,
	maxSize: Long,
	texture: ResourceLocation,
	u: Int, v: Int,
	width: Int, height: Int,
	startDirection: IDrawableAnimated.StartDirection,
	inverted: Boolean
): LoadingCache<Int, IDrawableAnimated> =
	CacheBuilder.newBuilder().maximumSize(maxSize).build(object : CacheLoader<Int, IDrawableAnimated>() {
		override fun load(key: Int): IDrawableAnimated =
			guiHelper.drawableBuilder(texture, u, v, width, height)
				.buildAnimated(key, startDirection, inverted)
	})

/**
 * @see createCachedArrows
 */
fun drawArrow(
	recipe: FluidEnergyRecipe<*>,
	cachedArrows: LoadingCache<Int, IDrawableAnimated>
): IDrawableAnimated = cachedArrows.getUnchecked(recipe.getTime())

fun recipeList(item: Item, multiplier: Int, repeatCount: Int): List<ItemStack> =
	List(repeatCount) { ItemStack(item, (it + 1) * multiplier) }

/**
 * @return A list of the provided [tag]
 */
fun itemTagToList(tag: TagKey<Item>): List<ItemStack> =
	BuiltInRegistries.ITEM.getTag(tag).get().map { it.value().defaultInstance }
//fun blockTagToList(tag: TagKey<Block>): List<ItemStack> =
//    BuiltInRegistries.BLOCK.getTag(tag).get().map { it.value().asItem().defaultInstance }