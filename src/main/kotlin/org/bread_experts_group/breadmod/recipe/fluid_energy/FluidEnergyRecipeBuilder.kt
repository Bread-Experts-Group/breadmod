package org.bread_experts_group.breadmod.recipe.fluid_energy

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.NonNullList
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.FluidIngredient

class FluidEnergyRecipeBuilder(
    private val results: List<Pair<ItemStack, Int>> = listOf()
) : RecipeBuilder {
    private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    private var ingredients = NonNullList.create<Ingredient>()
    private var fluidIngredients = NonNullList.create<FluidIngredient>()
    private var time = 0
    private var energy = 0

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder = this
    override fun group(groupName: String?): RecipeBuilder = this
    override fun getResult(): Item = results.first().first.item

    fun requiresItem(item: ItemStack) = this.also { ingredients.add(Ingredient.of(item)) }
    fun requiresItem(item: ItemStack, amount: Int) =
        this.also { ingredients.add(Ingredient.of(item.copyWithCount(amount))) }

    fun requiresItem(item: TagKey<Item>) = this.also { ingredients.add(Ingredient.of(item)) }

    fun requiresFluid(fluid: FluidStack) =
        this.also { fluidIngredients.add(FluidIngredient.of(fluid.copyWithAmount(1000))) }

    fun requiresFluid(fluid: Fluid, amount: Int) =
        this.also { fluidIngredients.add(FluidIngredient.of(FluidStack(fluid, amount))) }

    fun timeRequired(time: Int) = this.also { this.time = time }
    fun energyRequired(energy: Int) = this.also { this.energy = energy }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val outputList = mutableListOf<ItemStack>()
        results.forEach { outputList.add(it.first.copyWithCount(it.second)) }
        val recipe = FluidEnergyRecipe(ingredients, fluidIngredients, outputList, energy, time)
        val advancement: Advancement.Builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        criteria.forEach(advancement::addCriterion)
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")))
    }
}