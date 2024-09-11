package bread.mod.breadmod.recipe

import bread.mod.breadmod.registry.recipe.ModRecipeSerializers
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level

// todo port over the rest of this recipe
class BreadSliceRecipe : CustomRecipe(CraftingBookCategory.MISC) {
    override fun matches(
        input: CraftingInput,
        level: Level
    ): Boolean {
        TODO("Not yet implemented")
    }

    override fun assemble(
        input: CraftingInput,
        registries: HolderLookup.Provider
    ): ItemStack? {
        TODO("Not yet implemented")
    }

    override fun getRemainingItems(input: CraftingInput): NonNullList<ItemStack> =
        NonNullList.withSize(input.size(), ItemStack.EMPTY).also {
            var bread = 0
            var sword = ItemStack.EMPTY
            for (slot in 0 until it.size) {
                val stack = input.getItem(slot).copy()
                if (stack.`is`(ItemTags.SWORDS)) {
                    it[slot] = stack
                    sword = stack
                } else if (stack.`is`(Items.BREAD)) bread += 1
            }
        }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = (width * height) >= 2
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.BREAD_SLICE.get()
}