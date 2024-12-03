package org.bread_experts_group.breadmod.experimental.recipe.multi

import net.minecraft.core.NonNullList
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.experimental.recipe.BreadModRecipes

class MultiFluidTestRecipe(
    rFluidInputs: NonNullList<SizedFluidIngredient>,
    rFluidOutputs: List<FluidStack>,
    rTime: Int?,
    rEnergy: Int?
) : BreadModRecipes.MultiFluid(rFluidInputs, rFluidOutputs, rTime, rEnergy) {
    override fun getSerializer(): RecipeSerializer<*> {
        TODO("Not yet implemented")
    }

    override fun getType(): RecipeType<*> {
        TODO("Not yet implemented")
    }

    class Serializer() {}

    class Builder() {}
}