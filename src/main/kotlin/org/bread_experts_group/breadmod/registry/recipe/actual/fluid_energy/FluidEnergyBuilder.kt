package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements.Strategy
import net.minecraft.advancements.AdvancementRewards.Builder
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
import net.neoforged.neoforge.registries.DeferredItem

// todo probably introduce nullability on all the methods so the recipe provider can use null inputs for recipe building
class FluidEnergyBuilder(
	private val recipe: RecipeFunctionMulti,
	private val itemResults: List<Pair<Item, Int>> = listOf(),
	private val fluidResults: List<Pair<Fluid, Int>> = listOf()
) : RecipeBuilder {
	constructor(
		recipe: RecipeFunctionMulti,
		itemResults: Pair<Item, Int>? = null,
		fluidResults: Pair<Fluid, Int>? = null
	) : this(recipe, buildList { itemResults?.let(this::add) }, buildList { fluidResults?.let(this::add) })

	private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
	var time: Int = 0
	var energy: Int = 0
	override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
		this.criteria[name] = criterion
		return this
	}

	private fun invokeRecipe(): FluidEnergyRecipe = this.recipe.invoke(
		this.items,
		buildList {
			this@FluidEnergyBuilder.itemResults.forEach { this.add(ItemStack(it.first, it.second)) }
		}.toMutableList(),
		this.fluids,
		buildList {
			this@FluidEnergyBuilder.fluidResults.forEach { this.add(FluidStack(it.first, it.second)) }
		}.toMutableList(),
		this.time,
		this.energy
	)

	override fun group(groupName: String?): FluidEnergyBuilder = this
	fun timeRequired(time: Int): FluidEnergyBuilder = this.also { this.time = time }
	fun timeRequiredInSeconds(seconds: Int): FluidEnergyBuilder = this.also { this.time = seconds * 20 }
	fun energyRequired(energy: Int): FluidEnergyBuilder = this.also { this.energy = energy }
	private fun RecipeOutput.buildAdvancement(id: ResourceLocation): AdvancementHolder {
		val advancement = this.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(Builder.recipe(id))
			.requirements(Strategy.OR)
		return advancement.build(id.withPrefix("recipes/"))
	}

	var items: NonNullList<SizedIngredient> = NonNullList.create()
	var fluids: NonNullList<SizedFluidIngredient> = NonNullList.create()
	fun fluidRequired(fluid: Fluid?, amount: Int = 1000): FluidEnergyBuilder =
		this.also { this.fluids.add(fluid?.let { it1 -> SizedFluidIngredient.of(it1, amount) }) }

	fun fluidRequired(pair: Pair<Fluid, Int>?): FluidEnergyBuilder =
		pair?.let { this.fluidRequired(it.first, it.second) } ?: this

	fun fluidRequired(tag: TagKey<Fluid>?, amount: Int = 1000): FluidEnergyBuilder =
		this.also { this.fluids.add(tag?.let { it1 -> SizedFluidIngredient.of(it1, amount) }) }

	fun itemRequired(item: Item?, count: Int = 1): FluidEnergyBuilder =
		this.also { this.items.add(item?.let { it1 -> SizedIngredient.of(it1, count) }) }

	fun itemRequired(pair: Pair<Item, Int>?): FluidEnergyBuilder =
		pair?.let { this.itemRequired(it.first, pair.second) } ?: this

	fun itemRequired(item: DeferredItem<Item>?, count: Int = 1): FluidEnergyBuilder =
		item?.let { this.itemRequired(it.get(), count) } ?: this

	fun itemRequired(tag: TagKey<Item>?, count: Int = 1): FluidEnergyBuilder =
		this.also { this.items.add(tag?.let { it1 -> SizedIngredient.of(it1, count) }) }

	override fun getResult(): Item = this.itemResults[0].first
	override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
		require(this.items.isNotEmpty() || this.fluids.isNotEmpty()) { "Items or Fluids should have at least one ingredient!" }
		recipeOutput.accept(id, this.invokeRecipe(), recipeOutput.buildAdvancement(id))
	}
}