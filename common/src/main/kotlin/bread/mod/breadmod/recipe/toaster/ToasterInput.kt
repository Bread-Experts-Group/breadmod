package bread.mod.breadmod.recipe.toaster

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput

class ToasterInput(val stack: ItemStack, val count: Int) :
    RecipeInput {
    override fun getItem(index: Int): ItemStack? {
        if (index != 0) throw IllegalArgumentException("No item for index $index")
        return stack
    }

    override fun size(): Int = 1
}