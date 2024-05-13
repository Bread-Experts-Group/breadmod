package breadmod.compat.jei.category

import breadmod.ModMain.modLocation
import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlocks
import breadmod.util.renderFluid
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.tags.FluidTags
import net.minecraftforge.registries.ForgeRegistries

class DoughMachineRecipeCategory(private val guiHelper: IGuiHelper): FluidEnergyRecipeCategory(guiHelper) {
    val texture = modLocation("textures","gui","gui_dough_machine.png")
    private val recipeTime : Int = 0
//    private val progressArrow = guiHelper.createDrawable(texture, 147, 0, 24,17) // Can possibly repurpose this for a power meter that drops down in the recipe view
//    private val animatedProgressArrow = guiHelper.createAnimatedDrawable(progressArrow, 100, IDrawableAnimated.StartDirection.RIGHT, true)

    private val cachedArrows = CacheBuilder.newBuilder().maximumSize(24).build( object : CacheLoader<Int, IDrawableAnimated>() {
        override fun load(recipeTime: Int): IDrawableAnimated {
            return guiHelper.drawableBuilder(texture, 147, 0, 24, 17).buildAnimated(recipeTime, IDrawableAnimated.StartDirection.LEFT, false)
        }
    })

    private fun getArrow(recipe : FluidEnergyRecipe): IDrawableAnimated {
        var recipeTime = recipe.time
        if(recipeTime <= 0) {
            recipeTime = this.recipeTime
        }
        return this.cachedArrows.getUnchecked(recipeTime)
    }

    override fun getTitle(): Component = Component.translatable(ModBlocks.DOUGH_MACHINE_BLOCK.get().descriptionId)
    override fun getBackground(): IDrawable = guiHelper.createDrawable(texture, 0, 0, 147, 55)

    override fun draw(
        recipe: FluidEnergyRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val arrow = getArrow(recipe)
        arrow.draw(guiGraphics, 28, 19)

        val randomFluids = ForgeRegistries.FLUIDS.values.filter { it.`is`(FluidTags.WATER) }.random()
        guiGraphics.renderFluid(123f, 51f, 16, 28, randomFluids, false, Direction.NORTH)

        guiGraphics.blit(texture, 102, 4, 147, 17, 16, 47)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: FluidEnergyRecipe, focuses: IFocusGroup) {
        recipe.itemsRequired?.firstOrNull()?.let { builder.addSlot(RecipeIngredientRole.INPUT, 8, 18).addItemStack(it) }
        recipe.itemsOutput?.firstOrNull()?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 19).addItemStack(it) }
    }
}