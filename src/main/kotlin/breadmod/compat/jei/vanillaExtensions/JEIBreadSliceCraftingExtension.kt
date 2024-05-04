package breadmod.compat.jei.vanillaExtensions

import breadmod.registry.item.ModItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class JEIBreadSliceCraftingExtension: ICraftingCategoryExtension {
    override fun getWidth(): Int = 2
    override fun getHeight(): Int = 1

    // TODO there's gotta be a way to return a list of an item tag right?
    private val swordSet = listOf(
        Items.GOLDEN_SWORD,
        Items.DIAMOND_SWORD,
        Items.NETHERITE_SWORD,
        Items.WOODEN_SWORD,
        Items.IRON_SWORD,
        ModItems.BREAD_SWORD.get(),
        ModItems.RF_BREAD_SWORD.get()
    ).map { it.defaultInstance }

    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        craftingGridHelper: ICraftingGridHelper,
        focuses: IFocusGroup)
    {
        craftingGridHelper.createAndSetInputs(
            builder,
            listOf(
                swordSet,
                listOf(Items.BREAD.defaultInstance)
            ), width, height
        )
        craftingGridHelper.createAndSetOutputs(
            builder,
            listOf(ItemStack(ModItems.BREAD_SLICE.get(), 8)))
    }
}