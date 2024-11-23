package org.bread_experts_group.breadmod.recipe.crafting

import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.RecipeSerializer
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers

class BreadSlicingRecipe : AbstractCuttingRecipe(
    Items.BREAD.defaultInstance,
    ModItems.BREAD_SLICE.toStack(),
    8,
    ItemTags.SWORDS
) {
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.BREAD_SLICE.get()
}