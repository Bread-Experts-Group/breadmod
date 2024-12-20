package org.bread_experts_group.breadmod.registry.recipe.actual.wheat_crushing

import net.minecraft.core.HolderLookup.Provider
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class WheatCrusherRecipe(
	val recipeInput : ItemStack,
	val recipeOutput : ItemStack,
	val recipeEnergy : Int,
	val recipeTime : Int
) : Recipe<WheatCrusherRecipe.WheatCrusherInput> {
	override fun getSerializer() : RecipeSerializer<*> = ModRecipeSerializers.WHEAT_CRUSHING.get()
	override fun getType() : RecipeType<*> = ModRecipeTypes.WHEAT_CRUSHING.get()
	override fun matches(input : WheatCrusherInput, level : Level) : Boolean =
		Ingredient.of(this.recipeInput).test(input.input) && this.recipeTime >= 0 && this.recipeEnergy >= 0

	override fun assemble(input : WheatCrusherInput, registries : Provider) : ItemStack =
		this.recipeOutput.copyWithCount(input.count)

	override fun canCraftInDimensions(width : Int, height : Int) : Boolean = true
	override fun getResultItem(registries : Provider) : ItemStack = this.recipeOutput
	class WheatCrusherInput(val input : ItemStack, val count : Int) : RecipeInput {
		override fun getItem(index : Int) : ItemStack {
			return if (index == 0) this.input else ItemStack.EMPTY
//            if (index != 0) throw IllegalArgumentException("No item for index $index")
//            return input
		}

		override fun size() : Int = 2
	}
}