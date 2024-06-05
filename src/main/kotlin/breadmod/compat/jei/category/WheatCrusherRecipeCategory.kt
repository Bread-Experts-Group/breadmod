package breadmod.compat.jei.category

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.compat.jei.ModJEIRecipeTypes
import breadmod.recipe.fluidEnergy.WheatCrushingRecipe
import breadmod.registry.block.ModBlocks
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import mezz.jei.api.recipe.category.IRecipeCategory
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class WheatCrusherRecipeCategory(private val guiHelper: IGuiHelper): IRecipeCategory<WheatCrushingRecipe> {
    val texture = modLocation("textures","gui","jei","gui_wheat_crusher.png")
    private val recipeTime : Int = 0

    private val cachedArrows = CacheBuilder.newBuilder().maximumSize(48).build( object : CacheLoader<Int, IDrawableAnimated>() {
        override fun load(recipeTime: Int): IDrawableAnimated {
            return guiHelper.drawableBuilder(texture, 193, 0, 48, 9).buildAnimated(recipeTime, IDrawableAnimated.StartDirection.LEFT, false)
        }
    })

    private fun drawArrow(recipe : WheatCrushingRecipe): IDrawableAnimated {
        var recipeTime = recipe.time
        if(recipeTime <= 0) {
            recipeTime = this.recipeTime
        }
        return this.cachedArrows.getUnchecked(recipeTime)
    }

    private fun drawRecipeTime(recipe : WheatCrushingRecipe, guiGraphics: GuiGraphics) {
        if(recipe.time > 0) {
            val recipeTimeSeconds = recipe.time / 20
            val timeString = modTranslatable("jei", "generic", "recipe_time", args = listOf("$recipeTimeSeconds"))
            val minecraft = Minecraft.getInstance()
            val fontRenderer = minecraft.font
            guiGraphics.drawString(fontRenderer, timeString, 110 , 46, -8355712, false)
        }
    }

    override fun getRecipeType(): RecipeType<WheatCrushingRecipe> = ModJEIRecipeTypes.wheatCrusherRecipeType
    override fun getTitle(): Component = Component.translatable(ModBlocks.WHEAT_CRUSHER_BLOCK.get().descriptionId)
    override fun getBackground(): IDrawable = guiHelper.createDrawable(texture, 0, 0, 161, 65)
    override fun getIcon(): IDrawable = guiHelper.createDrawableItemStack(ModBlocks.WHEAT_CRUSHER_BLOCK.get().defaultInstance)

    private var step: Int = -32; private var timer: Int = 20
    override fun draw(
        recipe: WheatCrushingRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val arrow = drawArrow(recipe)
        arrow.draw(guiGraphics, 61, 27)
        drawRecipeTime(recipe, guiGraphics)
        guiGraphics.blit(texture, 142, 9, 193, 9, 16, 47)

        guiGraphics.blit(texture, 6, 16, 161, step, 32, 32)
        if(timer <= 0) {
            timer = 40
            if(step < 32) step += 32 else step = -32
        } else timer -= 2
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: WheatCrushingRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 43, 24)
            .addItemStacks(buildList {
                recipe.itemsRequired?.forEach { add(it) }
                recipe.itemsRequiredTagged?.forEach { (tagKey, amount) ->
                    ForgeRegistries.ITEMS.filter { it.defaultInstance.`is`(tagKey) }.forEach { item -> add(ItemStack(item, amount)) }
                }
            })

        recipe.itemsOutput?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 115, 24).addItemStacks(it) }
    }
}