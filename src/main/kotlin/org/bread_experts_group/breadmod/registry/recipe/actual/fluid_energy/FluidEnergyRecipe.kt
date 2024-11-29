package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

// todo this whole thing is fucked and needs a redo
open class FluidEnergyRecipe(
    val itemIngredients: NonNullList<SizedIngredient>,
    val fluidIngredients: NonNullList<SizedFluidIngredient>,
    val results: List<ItemStack>,
    val fluidResults: List<FluidStack>,
    val energy: Int,
    val time: Int
) : Recipe<FluidEnergyRecipe.FluidEnergyInput> {

    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.FLUID_ENERGY.get()

    fun getFirstItem(): ItemStack = itemIngredients[0].items[0]
    fun getFirstFluid(): FluidStack = fluidIngredients[0].fluids[0]

    // todo ugh..
    override fun matches(input: FluidEnergyInput, level: Level): Boolean {
        val ingredient = itemIngredients[0].ingredient()
        val count = itemIngredients[0].count()
        return itemIngredients.all { i -> input.itemsRequired.all { i.test(it) } }
//        return itemIngredients[0].test(input.getItem(0)) && time >= 0 && energy >= 0
        /*return if (input.count != itemIngredients.size && input.count != fluidIngredients.size) {
            false
        } else {
            val itemOkay = input.itemsRequired.all { r ->
                itemIngredients.first { it.ingredient().test(r) }?.let { it.count() >= input.count } ?: false
            } || input.itemsRequired.isEmpty()
            val fluidOkay = input.fluidsRequired.all { r ->
                fluidIngredients.first { it.ingredient().test(r) }?.let { it.amount() >= input.amount } ?: false
            } || input.fluidsRequired.isEmpty()

            itemOkay && fluidOkay
        }*/
    }

    override fun assemble(input: FluidEnergyInput, registries: Provider): ItemStack =
        results[0].copyWithCount(input.count)

    // todo work on
    fun assembleItems(input: FluidEnergyInput): List<ItemStack> =
        buildList { results.forEach { add(it.copyWithCount(input.count)) } }

    fun assembleFluids(input: FluidEnergyInput): List<FluidStack> =
        buildList { fluidResults.forEach { add(it.copyWithAmount(input.amount)) } }

    override fun getResultItem(registries: Provider): ItemStack = ItemStack.EMPTY
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    class FluidEnergyInput(
        val itemsRequired: List<ItemStack>,
        val fluidsRequired: List<FluidStack>
    ) : RecipeInput {
        var count: Int
        var amount: Int
        val stackedContents: StackedContents = StackedContents()

        init {
            var i = 0
            var f = 0

            for (stack: ItemStack in itemsRequired) {
                if (!stack.isEmpty) {
                    i++
                    stackedContents.accountStack(stack, 1)
                }
            }

            for (fluid: FluidStack in fluidsRequired) if (!fluid.isEmpty) f += fluid.amount

            amount = f
            count = i
        }

        override fun getItem(index: Int): ItemStack = if (index == 0) itemsRequired[0] else ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = fluidsRequired[index]
        override fun size(): Int = 2
    }
}