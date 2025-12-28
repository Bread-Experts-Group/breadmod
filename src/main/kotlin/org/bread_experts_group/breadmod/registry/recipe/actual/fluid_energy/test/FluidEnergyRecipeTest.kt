package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluid
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.InputOption
import java.math.BigDecimal

class FluidEnergyRecipeTest(
	rItemInputs: List<InputOption<Item>>,
	rItemOutputs: List<BigDescriptor<Item>>,
	rFluidInputs: List<InputOption<Fluid>>,
	rFluidOutputs: List<BigDescriptor<Fluid>>,
	rTime: ULong,
	rEnergy: BigDecimal?
) : FluidEnergyRecipe(rItemInputs, rItemOutputs, rFluidInputs, rFluidOutputs, rTime, rEnergy) {
	override val slots: Pair<List<Int>, List<Int>> = listOf(0, 1, 2, 3) to listOf(4, 5, 6, 7)

	override fun getType(): RecipeType<*> = ModRecipeTypes.FLUID_ENERGY_TEST.get()
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY_TEST.get()
}