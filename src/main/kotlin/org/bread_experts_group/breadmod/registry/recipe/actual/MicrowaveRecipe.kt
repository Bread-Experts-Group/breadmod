package org.bread_experts_group.breadmod.registry.recipe.actual

import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe

class MicrowaveRecipe(
	rItemInputs: List<BigDescriptor<Item>>,
	rItemOutputs: List<BigDescriptor<Item>>,
	rTime: ULong
) : FluidEnergyRecipe(rItemInputs, rItemOutputs, emptyList(), emptyList(), rTime, null) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.MICROWAVE.get()
	override fun getType(): RecipeType<*> = ModRecipeTypes.MICROWAVE.get()
}