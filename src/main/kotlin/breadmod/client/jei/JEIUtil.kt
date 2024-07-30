package breadmod.client.jei

import breadmod.ModMain
import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.helpers.IGuiHelper
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraftforge.registries.ForgeRegistries

/**
 * Helper functions to make building recipe categories easier
 */

fun drawRecipeTime(pRecipe: FluidEnergyRecipe, pGuiGraphics: GuiGraphics, pX: Int, pY: Int) {
    if(pRecipe.time > 0) {
        val recipeTimeSeconds = pRecipe.time / 20
        val timeString = ModMain.modTranslatable("jei", "generic", "recipe_time", args = listOf("$recipeTimeSeconds"))
        val minecraft = Minecraft.getInstance()
        val fontRenderer = minecraft.font
        pGuiGraphics.drawString(fontRenderer, timeString, pX , pY, -8355712, false)
    }
}

/**
 * Builder for creating a progressive arrow sprite based on recipe time
 */
fun createCachedArrows(
    pGuiHelper: IGuiHelper,
    pMaxSize: Long,
    pTexture: ResourceLocation,
    pU: Int,
    pV: Int,
    pWidth: Int,
    pHeight: Int,
    pStartDirection: IDrawableAnimated.StartDirection,
    pInverted: Boolean
): LoadingCache<Int, IDrawableAnimated> =
    CacheBuilder.newBuilder().maximumSize(pMaxSize).build( object : CacheLoader<Int, IDrawableAnimated>() {
        override fun load(recipeTime: Int): IDrawableAnimated =
            pGuiHelper.drawableBuilder(pTexture, pU, pV, pWidth, pHeight).buildAnimated(recipeTime, pStartDirection, pInverted)
    })

/**
 * @see createCachedArrows
 */
fun drawArrow(
    pRecipe: FluidEnergyRecipe,
    pRecipeTime: Int,
    pCachedArrows: LoadingCache<Int, IDrawableAnimated>
): IDrawableAnimated {
    var recipeTime = pRecipe.time
    if(recipeTime <= 0) recipeTime = pRecipeTime
    return pCachedArrows.getUnchecked(recipeTime)
}

/**
 * Constructs a recipe list with an [item], count [multiplier], and [pRepeatCount]
 */
fun recipeList(item: Item, multiplier: Int, pRepeatCount: Int): List<ItemStack> =
    List(pRepeatCount) { ItemStack(item, (it + 1) * multiplier) }

/**
 * Returns a list of the provided Item or Block Tag.
 * Returns an empty list if tag does not exist.
 */
fun itemTagToList(itemTag: TagKey<Item>): List<ItemStack> =
    ForgeRegistries.ITEMS.tags()?.getTag(itemTag)?.map { it.defaultInstance } ?: listOf()
fun blockTagToList(blockTag: TagKey<Block>): List<ItemStack> =
    ForgeRegistries.BLOCKS.tags()?.getTag(blockTag)?.map { it.asItem().defaultInstance } ?: listOf()