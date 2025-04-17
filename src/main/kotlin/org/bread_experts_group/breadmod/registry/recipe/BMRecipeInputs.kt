package org.bread_experts_group.breadmod.registry.recipe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.fluids.FluidStack

@Suppress("unused")
abstract class BMRecipeInputs(private val iSize: Int) : RecipeInput {
	override fun size(): Int = this.iSize
	class SingleItem(val iItem: ItemStack, iSize: Int) : BMRecipeInputs(iSize) {
		override fun getItem(index: Int): ItemStack = if (index == 0) this.iItem else ItemStack.EMPTY
	}

	class SingleFluid(val iFluid: FluidStack, iSize: Int) : BMRecipeInputs(iSize) {
		override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
		fun getFluid(index: Int): FluidStack = if (index == 0) this.iFluid else FluidStack.EMPTY
		override fun isEmpty(): Boolean = this.iFluid.isEmpty
	}

	class SingleFluidItem(val iItem: ItemStack, val iFluid: FluidStack, iSize: Int) : BMRecipeInputs(iSize) {
		override fun getItem(index: Int): ItemStack = if (index == 0) this.iItem else ItemStack.EMPTY
		fun getFluid(index: Int): FluidStack = if (index == 0) this.iFluid else FluidStack.EMPTY
		override fun isEmpty(): Boolean = this.iItem.isEmpty && this.iFluid.isEmpty
	}

	class MultiItem(val iItems: List<ItemStack>, iSize: Int) : BMRecipeInputs(iSize) {
		override fun getItem(index: Int): ItemStack = this.iItems[index]
	}

	class MultiFluid(val iFluids: List<FluidStack>, iSize: Int) : BMRecipeInputs(iSize) {
		override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
		fun getFluid(index: Int): FluidStack = this.iFluids[index]
		override fun isEmpty(): Boolean = this.iFluids.all { it.isEmpty }
	}

	class MultiFluidItem(
		private val iItems: List<ItemStack>,
		private val iFluids: List<FluidStack>,
		iSize: Int
	) : BMRecipeInputs(iSize) {
		override fun getItem(index: Int): ItemStack = this.iItems[index]
		fun getFluid(index: Int): FluidStack = this.iFluids[index]
	}
}