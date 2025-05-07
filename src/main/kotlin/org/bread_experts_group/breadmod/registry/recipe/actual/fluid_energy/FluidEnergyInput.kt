package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import net.neoforged.neoforge.fluids.FluidStack

open class FluidEnergyInput(
	val iItems: List<ItemStack>,
	val iFluids: List<FluidStack>
) : RecipeInput {
	constructor(item: ItemStack) : this(listOf(item), listOf())
	constructor(fluid: FluidStack) : this(listOf(), listOf(fluid))
	constructor(item: ItemStack, fluid: FluidStack) : this(listOf(item), listOf(fluid))

	override fun size(): Int = this.iItems.size
	override fun getItem(index: Int): ItemStack = this.iItems[index]
	override fun isEmpty(): Boolean = (super.isEmpty() || this.iItems.isEmpty()) && this.iFluids.isEmpty()
}