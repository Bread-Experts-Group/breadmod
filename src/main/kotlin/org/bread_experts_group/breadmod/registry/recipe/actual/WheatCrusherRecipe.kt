package org.bread_experts_group.breadmod.registry.recipe.actual

import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.FluidEnergyRecipe
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.InputOption
import java.math.BigDecimal

class WheatCrusherRecipe(
	rItemInputs: List<InputOption<Item>>,
	rItemOutputs: List<BigDescriptor<Item>>,
	rTime: ULong,
	rEnergy: BigDecimal?
) : FluidEnergyRecipe(rItemInputs, rItemOutputs, emptyList(), emptyList(), rTime, rEnergy) {
	override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.WHEAT_CRUSHING.get()
	override fun getType(): RecipeType<*> = ModRecipeTypes.WHEAT_CRUSHING.get()
}