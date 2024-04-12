package breadmod.recipe

import breadmod.registry.item.ModItems
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient

class CountedIngredient(private val pValues: List<CountedValue>) : Ingredient(pValues.stream()) {
    constructor(pStacks: Iterable<ItemStack>) : this(pStacks.map { CountedValue(it) })
    constructor(pStack: ItemStack) : this(listOf(pStack))

    override fun toJson(): JsonArray = JsonArray().also {
        pValues.forEach { stack -> it.add(stack.serialize()) }
    }

    class CountedValue(private val pItem: ItemStack) : ItemValue(pItem) {
        override fun serialize(): JsonObject = JsonObject().also {
            it.addProperty("item", ModItems.getLocation(pItem.item).toString())
            it.addProperty("count", pItem.count)
        }
    }
}