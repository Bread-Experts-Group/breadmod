package bread.mod.breadmod.recipe.toaster

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.block.state.BlockState

class ToasterInput(val state: BlockState, val stack: ItemStack, val count: Int) :
    RecipeInput {
    override fun getItem(index: Int): ItemStack? {
        if (index != 0) throw IllegalArgumentException("No item for index $index")
        return stack
    }

    override fun size(): Int = 1
}