package breadmod.compat.jei.category

import breadmod.ModMain.modLocation
import breadmod.ModMain.modTranslatable
import breadmod.block.entity.DoughMachineBlockEntity.Companion.INPUT_TANK_CAPACITY
import breadmod.block.entity.DoughMachineBlockEntity.Companion.OUTPUT_TANK_CAPACITY
import breadmod.recipe.FluidEnergyRecipe
import breadmod.registry.block.ModBlocks
import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import mezz.jei.api.forge.ForgeTypes
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraftforge.fluids.FluidStack
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

    private fun drawArrow(recipe : FluidEnergyRecipe): IDrawableAnimated {
        var recipeTime = recipe.time
        if(recipeTime <= 0) {
            recipeTime = this.recipeTime
        }
        return this.cachedArrows.getUnchecked(recipeTime)
    }

    private fun drawRecipeTime(recipe : FluidEnergyRecipe, guiGraphics: GuiGraphics, x : Int, y : Int) {
        if(recipe.time > 0) {
            val recipeTimeSeconds = recipe.time / 20
            val timeString = modTranslatable("jei", "dough_machine", "recipe_time", args = listOf("$recipeTimeSeconds"))
            val minecraft = Minecraft.getInstance()
            val fontRenderer = minecraft.font
            guiGraphics.drawString(fontRenderer, timeString, x , y, -8355712, false)
        }
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
        val arrow = drawArrow(recipe)
        arrow.draw(guiGraphics, 28, 19)
        drawRecipeTime(recipe, guiGraphics, 56, 42)
        guiGraphics.blit(texture, 102, 4, 147, 17, 16, 47)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: FluidEnergyRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 18)
            .addItemStacks(buildList {
                recipe.itemsRequired?.forEach { add(it) }
                recipe.itemsRequiredTagged?.forEach { (tagKey, amount) ->
                    ForgeRegistries.ITEMS.filter { it.defaultInstance.`is`(tagKey) }.forEach { item -> add(ItemStack(item, amount)) }
                }
            })

        if(recipe.fluidsRequiredTagged != null || recipe.fluidsRequired != null) { //TODO list builder for fluids
            val fluid = recipe.fluidsRequiredTagged?.firstOrNull()?.first?.let { ForgeRegistries.FLUIDS.tags()?.getTag(it) ?: listOf() }
            val fluidList = fluid?.map { it.defaultFluidState().type.bucket.defaultInstance }
            if (fluidList != null) {
                builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStacks(fluidList)
            }
        }

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 123,23)
            .addIngredients(ForgeTypes.FLUID_STACK, buildList {
                recipe.fluidsRequired?.forEach { add(it) }
                recipe.fluidsRequiredTagged?.forEach { (tagKey, amount) ->
                    ForgeRegistries.FLUIDS.filter { it.`is`(tagKey) }.forEach { fluid -> add(FluidStack(fluid, amount)) }
                }
            })
            .setFluidRenderer(INPUT_TANK_CAPACITY.toLong(), true, 16, 28)
        recipe.itemsOutput?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 19).addItemStacks(it) }
        recipe.fluidsOutput?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 123,4)
            .addIngredients(ForgeTypes.FLUID_STACK, it)
            .setFluidRenderer(OUTPUT_TANK_CAPACITY.toLong(), true, 16, 16)
        }
    }
}