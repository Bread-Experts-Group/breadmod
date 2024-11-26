package org.bread_experts_group.breadmod.registry.recipe.actual.experimental

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient

// todo recipes with multiple ingredients need to use SizedIngredient cause Ingredient doesn't account for more than one item
//  (when i get around to using Ingredient in recipe builders, check the dumpster fire that is FluidEnergyRecipe for some examples)
abstract class BreadModRecipes<T : RecipeInput>(val rTime: Int, val rEnergy: Int) : Recipe<T> {
    override fun matches(input: T, level: Level): Boolean = rTime >= 0 && rEnergy >= 0

    abstract class SingleItem(
        val rItemInput: SizedIngredient,
        val rItemOutput: ItemStack,
        rTime: Int,
        rEnergy: Int
    ) : BreadModRecipes<BMRecipeInputs.SingleItem>(rTime, rEnergy) {
        override fun matches(input: BMRecipeInputs.SingleItem, level: Level): Boolean =
            rItemInput.test(input.iItem) && super.matches(input, level)

        override fun assemble(input: BMRecipeInputs.SingleItem, provider: HolderLookup.Provider): ItemStack =
            rItemOutput.copyWithCount(input.iCount)

        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutput

        override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1
    }

    abstract class MultiItem(
        val rItemInputs: NonNullList<SizedIngredient>,
        val rItemOuputs: List<ItemStack>,
        rTime: Int,
        rEnergy: Int
    ) : BreadModRecipes<BMRecipeInputs.MultiItem>(rTime, rEnergy) {
        override fun matches(input: BMRecipeInputs.MultiItem, level: Level): Boolean =
            rItemInputs.all { rItem -> input.iItems.any { rItem.test(it) } } &&
                    super.matches(input, level)

        /**
         * @return The first item in [rItemInputs]
         */
        override fun assemble(input: BMRecipeInputs.MultiItem, registries: HolderLookup.Provider): ItemStack =
            rItemInputs[0].items.first().copyWithCount(input.iCount[0])

        /**
         * @return a copy of the first item in [rItemInputs]
         */
        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemInputs[0].items.first().copy()

        fun getResultItems(): List<ItemStack> = buildList { rItemOuputs.forEach { add(it.copy()) } }
        fun assembleOutputs(input: BMRecipeInputs.MultiItem): List<ItemStack> = buildList {
            repeat(rItemOuputs.size) { index -> add(rItemOuputs[index].copyWithCount(input.iCount[index])) }
        }
    }
}