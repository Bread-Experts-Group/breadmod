package breadmod.client.jei.category

import breadmod.ModMain.modLocation
import breadmod.block.entity.machine.DoughMachineBlockEntity.Companion.INPUT_TANK_CAPACITY
import breadmod.block.entity.machine.DoughMachineBlockEntity.Companion.OUTPUT_TANK_CAPACITY
import breadmod.client.jei.ModJEIRecipeTypes
import breadmod.client.jei.createCachedArrows
import breadmod.client.jei.drawArrow
import breadmod.client.jei.drawRecipeTime
import breadmod.recipe.fluidEnergy.DoughMachineRecipe
import breadmod.registry.block.ModBlocks
import breadmod.util.isTag
import mezz.jei.api.forge.ForgeTypes
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
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

class DoughMachineRecipeCategory(private val guiHelper: IGuiHelper): FluidEnergyRecipeCategory<DoughMachineRecipe>(guiHelper) {
    val texture = modLocation("textures","gui","jei","gui_dough_machine.png")
    private val recipeTime : Int = 0
    private val cachedArrows = createCachedArrows(guiHelper, 24, texture, 147, 0, 24, 17, IDrawableAnimated.StartDirection.LEFT, false)

    override fun getTitle(): Component = Component.translatable(ModBlocks.DOUGH_MACHINE_BLOCK.get().descriptionId)
    override fun getBackground(): IDrawable = guiHelper.createDrawable(texture, 0, 0, 147, 55)
    override fun getRecipeType(): RecipeType<DoughMachineRecipe> = ModJEIRecipeTypes.doughMachineRecipeType

    override fun draw(
        recipe: DoughMachineRecipe,
        recipeSlotsView: IRecipeSlotsView,
        guiGraphics: GuiGraphics,
        mouseX: Double,
        mouseY: Double
    ) {
        val arrow = drawArrow(recipe, recipeTime, cachedArrows)
        arrow.draw(guiGraphics, 28, 19)
        drawRecipeTime(recipe, guiGraphics, 55, 42)
        guiGraphics.blit(texture, 102, 4, 147, 17, 16, 47)
    }

    override fun setRecipe(builder: IRecipeLayoutBuilder, recipe: DoughMachineRecipe, focuses: IFocusGroup) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 18)
            .addItemStacks(buildList {
                recipe.itemsRequired?.forEach { add(it) }
                recipe.itemsRequiredTagged?.forEach { (tagKey, amount) ->
                    ForgeRegistries.ITEMS.filter { it.defaultInstance.`is`(tagKey) }.forEach { item -> add(ItemStack(item, amount)) }
                }
            })

        val buckets = mutableListOf<ItemStack>()
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 123,23)
            .addIngredients(ForgeTypes.FLUID_STACK, buildList {
                recipe.fluidsRequired?.forEach { add(it) }
                recipe.fluidsRequiredTagged?.forEach { (tagKey, amount) ->
                    ForgeRegistries.FLUIDS.filter { it.isTag(tagKey) }.forEach { fluid -> add(FluidStack(fluid, amount)); buckets.add(fluid.bucket.defaultInstance) }
                }
            })
            .setFluidRenderer(INPUT_TANK_CAPACITY.toLong(), true, 16, 28)
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 0).addItemStacks(buckets)

        recipe.itemsOutput?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 60, 19).addItemStacks(it) }
        recipe.fluidsOutput?.let { builder.addSlot(RecipeIngredientRole.OUTPUT, 123,4)
            .addIngredients(ForgeTypes.FLUID_STACK, it)
            .setFluidRenderer(OUTPUT_TANK_CAPACITY.toLong(), true, 16, 16)
        }
    }
}