package org.bread_experts_group.breadmod.registry.recipe.actual.dough_machine

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.minecraft.world.level.Level
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.FluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class DoughMachineRecipe(
    val recipeInput: ItemStack,
    val recipeOutput: ItemStack,
    val recipeFluidInput: FluidStack,
    val recipeFluidOutput: FluidStack,
    val recipeEnergy: Int,
    val recipeTime: Int
) : Recipe<DoughMachineRecipe.DoughMachineInput> {

    override fun matches(input: DoughMachineInput, level: Level): Boolean =
        Ingredient.of(recipeInput).test(input.input) && FluidIngredient.of(recipeFluidInput).test(input.fluidInput) &&
                recipeTime >= 0 && recipeEnergy >= 0

    override fun assemble(input: DoughMachineInput, registries: HolderLookup.Provider): ItemStack =
        recipeOutput.copyWithCount(input.count)

    fun assembleFluid(input: DoughMachineInput): FluidStack =
        recipeFluidOutput.copyWithAmount(input.amount)

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = width * height >= 1

    override fun getResultItem(registries: HolderLookup.Provider): ItemStack = recipeOutput

    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.DOUGH_MACHINE.get()

    override fun getType(): RecipeType<*> = ModRecipeTypes.DOUGH_MACHINE.get()

    class DoughMachineInput(
        val input: ItemStack, val count: Int,
        val fluidInput: FluidStack, val amount: Int
    ) : RecipeInput {
        override fun getItem(index: Int): ItemStack =
            if (index == 0) input else ItemStack.EMPTY

        override fun size(): Int = 1

    }
}