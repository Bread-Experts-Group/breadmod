package breadmod.compat.jei.category

import breadmod.ModMain.modLocation
import breadmod.compat.jei.ModJEIRecipeTypes
import breadmod.compat.jei.createCachedArrows
import breadmod.compat.jei.drawArrow
import breadmod.compat.jei.drawRecipeTime
import breadmod.recipe.fluidEnergy.ToasterRecipe
import breadmod.registry.block.ModBlocks
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.drawable.IDrawable
import mezz.jei.api.gui.drawable.IDrawableAnimated
import mezz.jei.api.gui.ingredient.IRecipeSlotsView
import mezz.jei.api.helpers.IGuiHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.RecipeIngredientRole
import mezz.jei.api.recipe.RecipeType
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraftforge.registries.ForgeRegistries

class ToasterRecipeCategory(private val guiHelper: IGuiHelper): FluidEnergyRecipeCategory<ToasterRecipe>(guiHelper) {
    val texture = modLocation("textures","gui","jei","gui_toaster.png")
    private val recipeTime: Int = 0
    private val cachedArrows = createCachedArrows(guiHelper, 22, texture, 66, 0, 29, 22, IDrawableAnimated.StartDirection.TOP, false)

    override fun getRecipeType(): RecipeType<ToasterRecipe> = ModJEIRecipeTypes.toasterRecipeType
    override fun getTitle(): Component = Component.translatable(ModBlocks.TOASTER.get().descriptionId)
    override fun getBackground(): IDrawable = guiHelper.createDrawable(texture, 0, 0, 66, 66)
    override fun getIcon(): IDrawable = guiHelper.createDrawableItemStack(ModBlocks.TOASTER.get().defaultInstance)

    override fun draw(
        recipe: ToasterRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val arrow = drawArrow(recipe, recipeTime, cachedArrows)
        arrow.draw(guiGraphics, 27, 10)
        drawRecipeTime(recipe, guiGraphics, 40, 52)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: ToasterRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 9, 46).addItemStack(ModBlocks.TOASTER.get().defaultInstance)
        builder.addSlot(RecipeIngredientRole.INPUT, 9, 4)
            .addItemStacks(buildList {
                recipe.itemsRequired?.forEach { add(it) }
                recipe.itemsRequiredTagged?.forEach { (tagKey, amount) ->
                    ForgeRegistries.ITEMS.filter { it.defaultInstance.`is`(tagKey) }.forEach { item -> add(ItemStack(item, amount)) }
                }
            })

        recipe.itemsOutput?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 41, 34).addItemStacks(it) }
    }
}