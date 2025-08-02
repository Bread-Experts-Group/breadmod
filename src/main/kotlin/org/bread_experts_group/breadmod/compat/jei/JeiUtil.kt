package org.bread_experts_group.breadmod.compat.jei

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.mojang.math.Axis
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.helpers.IGuiHelper
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.registries.DeferredItem
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.client.render.translate
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe

/**
 * Draws a recipe time string
 */
fun drawRecipeTime(recipe: FluidEnergyRecipe, guiGraphics: GuiGraphics, x: Int, y: Int) {
	if (recipe.rTime > 0u) {
		val recipeTimeSeconds = recipe.rTime / 20u
		val timeString = modTranslatable("jei", "generic", "recipe_time", args = listOf("$recipeTimeSeconds"))
		guiGraphics.drawString(localClient.font, timeString, x, y, -8355712, false)
	}
}

fun drawEnergyTooltip(recipe: FluidEnergyRecipe): Component =
	modTranslatable("jei", "generic", "recipe_energy", args = listOf("${recipe.rEnergy}"))

fun IGuiHelper.buildBackground(width: Int, height: Int): IDrawable =
	this.drawableBuilder(ModGuiElements.FLAT_BACKGROUND.location, 0, 0, width, height).build()

fun <T : Item> IGuiHelper.drawableItemStack(registryObject: DeferredItem<T>): IDrawable =
	this.createDrawableItemStack(registryObject.toStack())

/**
 * Builder for creating a progressive arrow sprite based on recipe time
 */
fun createCachedArrow(
	guiHelper: IGuiHelper,
	maxSize: Long,
	texture: ResourceLocation,
	u: Int, v: Int,
	width: Int, height: Int,
	startDirection: IDrawableAnimated.StartDirection,
	inverted: Boolean = false
): LoadingCache<ULong, IDrawableAnimated> =
	CacheBuilder.newBuilder().maximumSize(maxSize).build(object : CacheLoader<ULong, IDrawableAnimated>() {
		override fun load(key: ULong): IDrawableAnimated =
			guiHelper.drawableBuilder(texture, u, v, width, height)
				.setTextureSize(width, height)
				.buildAnimated(1, startDirection, inverted)
	}) // TODO Long tick

/**
 * @see createCachedArrow
 */
fun <T : IDrawableAnimated> getCachedArrow(
	recipe: FluidEnergyRecipe,
	cachedArrows: LoadingCache<ULong, T>
): T = cachedArrows.getUnchecked(recipe.rTime)

fun <T : FluidEnergyRecipe> drawRotatedArrow(
	guiGraphics: GuiGraphics,
	recipe: T,
	arrow: LoadingCache<ULong, IDrawableAnimated>,
	x: Int,
	y: Int,
	rotation: Float
) {
	val pose = guiGraphics.pose()
	pose.pushPose()
	pose.translate(x, y, 0)
	pose.mulPose(Axis.ZN.rotationDegrees(rotation))
	getCachedArrow(recipe, arrow).draw(guiGraphics)
	pose.popPose()
}

fun recipeList(item: Item, multiplier: Int, repeatCount: Int): List<ItemStack> =
	List(repeatCount) { ItemStack(item, (it + 1) * multiplier) }

/**
 * @return A list of the provided [tag]
 */
fun itemTagToList(tag: TagKey<Item>): List<ItemStack> =
	BuiltInRegistries.ITEM.getTag(tag).get().map { it.value().defaultInstance }
//fun blockTagToList(tag: TagKey<Block>): List<ItemStack> =
//    BuiltInRegistries.BLOCK.getTag(tag).get().map { it.value().asItem().defaultInstance }