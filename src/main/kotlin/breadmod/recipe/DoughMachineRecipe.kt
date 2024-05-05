package breadmod.recipe

import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level

open class DoughMachineRecipe(pId: ResourceLocation, pCategory: CraftingBookCategory, pIngredient: Ingredient, pResult: ItemStack) : CustomRecipe(pId, pCategory) {
    override fun matches(p0: CraftingContainer, p1: Level): Boolean {
        TODO("Not yet implemented")
    }

    override fun assemble(p0: CraftingContainer, p1: RegistryAccess): ItemStack {
        TODO("Not yet implemented")
    }

    override fun canCraftInDimensions(p0: Int, p1: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented")
    }
}