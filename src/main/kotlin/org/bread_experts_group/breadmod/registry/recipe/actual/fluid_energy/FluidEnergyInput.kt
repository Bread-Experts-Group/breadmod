package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeInput
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedEnergyHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedItemHandler
import java.math.BigDecimal

class FluidEnergyInput(
	val item: ExtendedItemHandler? = null,
	val fluid: ExtendedFluidHandler? = null,
	val energy: ExtendedEnergyHandler? = null,
	var lastConsumed: BigDecimal = BigDecimal.ZERO
) : RecipeInput {
	override fun size(): Int = throw UnsupportedOperationException()
	override fun getItem(index: Int): ItemStack = throw UnsupportedOperationException()
	override fun isEmpty(): Boolean = this.item == null && this.fluid != null && this.energy != null
}