package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

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
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient

class FluidEnergyRecipeBuilder(
    private val results: List<ItemStack> = listOf(),
    private val fluidResults: List<FluidStack> = listOf()
) : RecipeBuilder {
    private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    private var ingredients = NonNullList.create<SizedIngredient>()
    private var fluidIngredients = NonNullList.create<SizedFluidIngredient>()
    private var time = 0
    private var energy = 0

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder = this
    override fun group(groupName: String?): RecipeBuilder = this
    override fun getResult(): Item = results.first().item

    fun requiresItem(item: ItemStack): FluidEnergyRecipeBuilder = this.also { ingredients.add(SizedIngredient.of(item.item, 1)) }
    fun requiresItem(item: ItemStack, amount: Int): FluidEnergyRecipeBuilder =
        this.also { ingredients.add(SizedIngredient.of(item.item, amount)) }

    fun requiresItem(item: TagKey<Item>): FluidEnergyRecipeBuilder = this.also { ingredients.add(SizedIngredient.of(item, 1)) }
    fun requiresItem(item: TagKey<Item>, amount: Int): FluidEnergyRecipeBuilder = this.also { ingredients.add(SizedIngredient.of(item, amount)) }

    fun requiresFluid(fluid: FluidStack): FluidEnergyRecipeBuilder =
        this.also { fluidIngredients.add(SizedFluidIngredient.of(fluid.fluid, 1000)) }

    fun requiresFluid(fluid: Fluid, amount: Int): FluidEnergyRecipeBuilder =
        this.also { fluidIngredients.add(SizedFluidIngredient.of(FluidStack(fluid, amount))) }

    fun timeRequired(time: Int): FluidEnergyRecipeBuilder = this.also { this.time = time }
    fun energyRequired(energy: Int): FluidEnergyRecipeBuilder = this.also { this.energy = energy }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val itemOutputList = buildList { results.forEach { add(it) } }
        val fluidOutputList = buildList { fluidResults.forEach { add(it) } }
        val recipe = FluidEnergyRecipe(ingredients, fluidIngredients, itemOutputList, fluidOutputList, energy, time)
        val advancement: Advancement.Builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)

        criteria.forEach(advancement::addCriterion)
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")))
    }
}