package breadmod.compat.jei.vanillaExtensions

import breadmod.registry.item.ModItems
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder
import mezz.jei.api.gui.ingredient.ICraftingGridHelper
import mezz.jei.api.recipe.IFocusGroup
import mezz.jei.api.recipe.category.extensions.vanilla.crafting.ICraftingCategoryExtension
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraftforge.registries.ForgeRegistries

class JEIBreadSliceCraftingExtension: ICraftingCategoryExtension {
    override fun getWidth(): Int = 2
    override fun getHeight(): Int = 1

    override fun setRecipe(
        builder: IRecipeLayoutBuilder,
        craftingGridHelper: ICraftingGridHelper,
        focuses: IFocusGroup)
    {
        val swordTags = ForgeRegistries.ITEMS.tags()?.getTag(ItemTags.SWORDS) ?: listOf()
        val swordList = swordTags.map { it.defaultInstance }

        craftingGridHelper.createAndSetInputs(
            builder,
            listOf(
                swordList,
                listOf(Items.BREAD.defaultInstance)
            ), width, height
        )
        craftingGridHelper.createAndSetOutputs(
            builder,
            listOf(ItemStack(ModItems.BREAD_SLICE.get(), 8)))
    }
}