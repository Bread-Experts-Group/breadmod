package org.bread_experts_group.breadmod.experimental.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.fluids.FluidStack

@Suppress("unused")
abstract class BMRecipeInputs(private val iSize: Int) : RecipeInput {
    override fun size(): Int = iSize

    class SingleItem(val iItem: ItemStack, val iCount: Int, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = if (index == 0) iItem else ItemStack.EMPTY
    }

    class SingleFluid(private val iFluid: FluidStack, val iAmount: Int, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = if (index == 0) iFluid else FluidStack.EMPTY
    }

    class SingleFluidItem(
        private val iItem: ItemStack,
        val iCount: Int,
        private val iFluid: FluidStack,
        val iAmount: Int,
        iSize: Int
    ) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = if (index == 0) iItem else ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = if (index == 0) iFluid else FluidStack.EMPTY
    }

    class MultiItem(val iItems: List<ItemStack>, val iCount: List<Int>, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = iItems[index]
    }

    class MultiFluid(private val iFluids: List<FluidStack>, val iAmount: List<Int>, iSize: Int) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
        fun getFluid(index: Int): FluidStack = iFluids[index]
    }

    class MultiFluidItem(
        private val iItems: List<ItemStack>,
        val iCount: List<Int>,
        private val iFluids: List<FluidStack>,
        val iAmount: List<Int>,
        iSize: Int
    ) : BMRecipeInputs(iSize) {
        override fun getItem(index: Int): ItemStack = iItems[index]
        fun getFluid(index: Int): FluidStack = iFluids[index]
    }
}