package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeInputs

open class FluidEnergyInput(
	val iItems: List<ItemStack>,
	val iFluids: List<FluidStack>
) : BMRecipeInputs(1) {
	constructor(item: ItemStack) : this(listOf(item), listOf())
	constructor(fluid: FluidStack) : this(listOf(), listOf(fluid))
	constructor(item: ItemStack, fluid: FluidStack) : this(listOf(item), listOf(fluid))

	override fun getItem(index: Int): ItemStack = this.iItems[index]
	override fun isEmpty(): Boolean = (super.isEmpty() || this.iItems.isEmpty()) && this.iFluids.isEmpty()
}