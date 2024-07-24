package breadmod.datagen.recipe

import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.tags.TagKey
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

interface IItemBearingRecipeBuilder: RecipeBuilder {
    val itemsRequired: MutableList<ItemStack>
    val itemsRequiredTagged: MutableList<Pair<TagKey<ItemLike>, Int>>
    fun requiresItem(itemStack: ItemStack) = this.also { itemsRequired.add(itemStack) }
    fun requiresItem(item: ItemLike, count: Int = 1) = requiresItem(ItemStack(item, count))
    fun requiresItem(item: TagKey<ItemLike>, count: Int = 1) = this.also { itemsRequiredTagged.add(item to count) }
}