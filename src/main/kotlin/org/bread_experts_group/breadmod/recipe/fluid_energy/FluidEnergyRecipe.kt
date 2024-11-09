package org.bread_experts_group.breadmod.recipe.fluid_energy

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.util.RecipeMatcher
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.FluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class FluidEnergyRecipe(
    val itemIngredients: NonNullList<Ingredient>,
    val fluidIngredients: NonNullList<FluidIngredient>,
    val results: List<ItemStack>,
    val energy: Int,
    val time: Int
) : Recipe<FluidEnergyRecipe.FluidEnergyInput> {
    val isSimple: Boolean = itemIngredients.stream().allMatch(Ingredient::isSimple) &&
            fluidIngredients.stream().allMatch(FluidIngredient::isSimple)

    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.FLUID_ENERGY.get()

    override fun matches(input: FluidEnergyInput, level: Level): Boolean {
        return if (input.count != itemIngredients.size || input.count != fluidIngredients.size) {
            false
        } else if (!isSimple) {
            val nonEmptyItems = ArrayList<ItemStack>(input.count)
            val nonEmptyFluids = ArrayList<FluidStack>(input.count)
            for (item in input.itemsRequired) nonEmptyItems.add(item)
            for (fluid in input.fluidsRequired) nonEmptyFluids.add(fluid)
            val itemMatch = RecipeMatcher.findMatches(nonEmptyItems, itemIngredients) != null
            val fluidMatch = RecipeMatcher.findMatches(nonEmptyFluids, fluidIngredients) != null

            return itemMatch || fluidMatch
        } else false /*{
            if (input.size == 1 && ingredients.size == 1)
                ingredients.first().test(input.getItem(0))
            else
                input.stackedContents.canCraft(this, null)
        }*/
        // todo figure this out later
    }

    override fun assemble(input: FluidEnergyInput, registries: Provider): ItemStack = ItemStack.EMPTY

    fun assembleOutputs(input: FluidEnergyInput, registries: Provider): List<ItemStack> = results

    override fun getResultItem(registries: Provider): ItemStack = ItemStack.EMPTY
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    class FluidEnergyInput(
        val size: Int,
        val itemsRequired: List<ItemStack>,
        val fluidsRequired: List<FluidStack>
    ) : RecipeInput {
        var count: Int
        val stackedContents = StackedContents()

        init {
            var i = 0

            for (stack: ItemStack in itemsRequired) {
                if (!stack.isEmpty) {
                    i++
                    stackedContents.accountStack(stack, 1)
                }
            }

            count = i
        }

        override fun getItem(index: Int): ItemStack = itemsRequired[index]
        fun getFluid(index: Int): FluidStack = fluidsRequired[index]
        override fun size(): Int = this.size
    }
}