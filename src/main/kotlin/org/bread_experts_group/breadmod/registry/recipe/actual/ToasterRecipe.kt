package org.bread_experts_group.breadmod.registry.recipe.actual

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipeMulti

class ToasterRecipe(
	rItemInputs: NonNullList<SizedIngredient>,
	rItemOutputs: MutableList<ItemStack>,
	rFluidInputs: NonNullList<SizedFluidIngredient>,
	rFluidOutputs: MutableList<FluidStack>,
	rTime: Int?,
	rEnergy: Int?
) : FluidEnergyRecipeMulti(rItemInputs, rItemOutputs, rFluidInputs, rFluidOutputs, rTime, rEnergy) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.TOASTER.get()
	override fun getType(): RecipeType<*> = ModRecipeTypes.TOASTING.get()
}