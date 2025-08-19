package org.bread_experts_group.breadmod.registry.recipe.actual

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

class DoughMachineRecipe(
	rItemInputs: List<InputOption<Item>>,
	rItemOutputs: List<BigDescriptor<Item>>,
	rFluidInputs: List<InputOption<Fluid>>,
	rFluidOutputs: List<BigDescriptor<Fluid>>,
	rTime: ULong,
	rEnergy: BigDecimal?
) : FluidEnergyRecipe(rItemInputs, rItemOutputs, rFluidInputs, rFluidOutputs, rTime, rEnergy) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.DOUGH_MACHINE.get()
	override fun getType(): RecipeType<*> = ModRecipeTypes.DOUGH_MACHINE.get()
}