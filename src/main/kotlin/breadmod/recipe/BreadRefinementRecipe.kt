package breadmod.recipe

import breadmod.block.registry.ModBlocks
//import breadmod.recipe.ModRecipeSerializers.BREAD_REFINEMENT
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*

class BreadRefinementRecipe(
    pId: ResourceLocation, pGroup: String, pCategory: CookingBookCategory,
    pIngredient: Ingredient, pResult: ItemStack, pExperience: Float, pCookingTime: Int,
) :
    AbstractCookingRecipe(
        ModRecipeTypes.BREAD_REFINEMENT,
        pId, pGroup, pCategory, pIngredient, pResult, pExperience, pCookingTime
    ) {

    override fun getToastSymbol(): ItemStack = ItemStack(ModBlocks.BREAD_FURNACE_BLOCK.get())
    override fun getSerializer(): RecipeSerializer<*> = RecipeSerializer.SMELTING_RECIPE
}