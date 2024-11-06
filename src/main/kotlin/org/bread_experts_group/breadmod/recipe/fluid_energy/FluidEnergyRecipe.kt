package org.bread_experts_group.breadmod.recipe.fluid_energy

import net.minecraft.core.HolderLookup
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import com.mojang.datafixers.util.Pair
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.crafting.Ingredient
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeSerializers
import org.bread_experts_group.breadmod.registry.recipe.ModRecipeTypes

class FluidEnergyRecipe(
    val recipeInput: List<ItemStack>? = null,
    val recipeInputTagged: List<TagKey<Item>>? = null,
    val recipeOutput: List<ItemStack>? = null
) : Recipe<FluidEnergyRecipe.FluidEnergyInput> {
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.FLUID_ENERGY.get()
    override fun getType(): RecipeType<*> = ModRecipeTypes.FLUID_ENERGY.get()

    override fun matches(input: FluidEnergyInput, level: Level): Boolean {
        val okay = recipeInput?.all { recipeIn ->
            input.itemsRequired?.all { inputIn ->
                Ingredient.of(recipeIn).test(inputIn)
            } ?: false
        } ?: true && recipeInputTagged?.all { recipeInTag ->
            input.itemsRequiredTagged?.all { inputInTag ->
                Ingredient.of(BuiltInRegistries.ITEM.get(recipeInTag.location))
                    .test(BuiltInRegistries.ITEM.get(inputInTag.first.location).defaultInstance)
            } ?: false
        } ?: true

        return okay
    }

    override fun assemble(input: FluidEnergyInput, registries: Provider): ItemStack = ItemStack.EMPTY

    fun assembleOutputs(input: FluidEnergyInput, registries: Provider): List<ItemStack> =
        recipeOutput ?: listOf()

    override fun getResultItem(registries: Provider): ItemStack = ItemStack.EMPTY
    override fun canCraftInDimensions(width: Int, height: Int): Boolean = true

    class FluidEnergyInput(
        val size: Int,
        val itemsRequired: List<ItemStack>? = null,
        val itemsRequiredTagged: List<Pair<TagKey<Item>, Int>>? = null
    ) : RecipeInput {
        override fun getItem(index: Int): ItemStack = ItemStack.EMPTY
        override fun size(): Int = this.size
    }
}