package bread.mod.breadmod.recipe.toaster

import bread.mod.breadmod.registry.recipe.ModRecipeSerializers
import bread.mod.breadmod.registry.recipe.ModRecipeTypes
import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level

class ToasterRecipe(
    val inputItem: ItemStack, val result: ItemStack
) : Recipe<ToasterInput> {
    override fun getIngredients(): NonNullList<Ingredient> {
        val list: NonNullList<Ingredient> = NonNullList.create()
        list.add(Ingredient.of(inputItem))
        return list
    }

    override fun matches(
        input: ToasterInput,
        level: Level
    ): Boolean =
        Ingredient.of(inputItem).test(input.stack)

    override fun assemble(
        input: ToasterInput,
        registries: HolderLookup.Provider
    ): ItemStack = result.copyWithCount(2)

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1
    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = result.copyWithCount(2)
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.TOASTING.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.TOASTING.get()
}