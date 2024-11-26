package org.bread_experts_group.breadmod.registry.recipe.actual.experimental

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.fluids.FluidStack

abstract class BMRecipeInputs(private val iSize: Int) : RecipeInput {
    override fun size(): Int = iSize

    class SingleItem(val iItem: ItemStack, val iCount: Int, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = if (index == 0) iItem else ItemStack.EMPTY
    }

    class SingleFluid(val iFluid: FluidStack, val iAmount: Int, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = if (index == 0) iFluid else FluidStack.EMPTY
    }

    class SingleFluidItem(
        val iItem: ItemStack,
        val iCount: Int,
        val iFluid: FluidStack,
        val iAmount: Int,
        iSize: Int
    ) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = if (index == 0) iItem else ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = if (index == 0) iFluid else FluidStack.EMPTY
    }

    class MultiItem(val iItems: List<ItemStack>, val iCount: List<Int>, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = iItems[0]
    }

    class MultiFluid(val iFluids: List<FluidStack>, val iAmount: List<Int>, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = iFluids[index]
    }

    class MultiFluidItem(
        val iItems: List<ItemStack>,
        val iCount: List<Int>,
        val iFluids: List<FluidStack>,
        val iAmount: List<Int>,
        val iSize: Int
    ) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = iItems[index]
        fun getFluid(index: Int): FluidStack = iFluids[index]
    }
}