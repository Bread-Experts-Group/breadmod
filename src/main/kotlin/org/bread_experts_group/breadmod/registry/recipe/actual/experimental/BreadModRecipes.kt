package org.bread_experts_group.breadmod.registry.recipe.actual.experimental

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient

abstract class BreadModRecipes<T : RecipeInput>(val rTime: Int?, val rEnergy: Int?) : Recipe<T> {
    override fun matches(input: T, level: Level): Boolean = rTime!! >= 0 && rEnergy!! >= 0

    /**
     * @return true if [rTime] is 0
     */
    fun isInstant(): Boolean = rTime == 0

    /**
     * @return true if [rEnergy] is 0
     */
    fun usesNoEnergy(): Boolean = rEnergy == 0

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

        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutput.copy()

        override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1
    }

    abstract class MultiItem(
        val rItemInputs: NonNullList<SizedIngredient>,
        val rItemOutputs: List<ItemStack>,
        rTime: Int?,
        rEnergy: Int?
    ) : BreadModRecipes<BMRecipeInputs.MultiItem>(rTime, rEnergy) {

        // todo account for split stacks of matching items
        override fun matches(input: BMRecipeInputs.MultiItem, level: Level): Boolean =
            rItemInputs.all { rItem -> input.iItems.any { iItem -> rItem.test(iItem) } } && super.matches(input, level)

        /**
         * @return The first item in [rItemOutputs]
         */
        override fun assemble(input: BMRecipeInputs.MultiItem, registries: HolderLookup.Provider): ItemStack =
            rItemOutputs[0].copyWithCount(input.iCount[0])

        /**
         * @return a copy of the first item in [rItemOutputs]
         */
        override fun getResultItem(registries: HolderLookup.Provider): ItemStack = rItemOutputs[0].copy()

        fun getResultItems(): List<ItemStack> = buildList { rItemOutputs.forEach { add(it.copy()) } }
        fun assembleOutputs(input: BMRecipeInputs.MultiItem): List<ItemStack> {
            return buildList {
                repeat(rItemOutputs.size) { index -> add(rItemOutputs[index].copyWithCount(input.iCount[index])) }
            }
        }
    }
}