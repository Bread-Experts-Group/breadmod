package org.bread_experts_group.breadmod.registry.recipe.actual.experimental.single

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.common.crafting.SizedIngredient
import org.bread_experts_group.breadmod.registry.recipe.actual.experimental.BreadModRecipes

class SingleItemTestRecipe(
    rItemInput: SizedIngredient,
    rItemOutput: ItemStack,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes.SingleItem(rItemInput, rItemOutput, rTime, rEnergy) {
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height == 1
    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented")
    }

    override fun getType(): RecipeType<*> {
        TODO("Not yet implemented")
    }
}