package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy_recipe

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeInputs

open class FluidEnergyInput(
	val iItems : List<ItemStack>,
	val iCount : List<Int>,
	val iFluids : List<FluidStack>,
	val iAmount : List<Int>
) : BMRecipeInputs(1) {
	override fun getItem(index : Int) : ItemStack = this.iItems[index]
	override fun isEmpty() : Boolean = (super.isEmpty() || this.iItems.isEmpty()) && this.iFluids.isEmpty()
}