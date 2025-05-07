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

class FluidEnergyBuilder(
	private val recipe: RecipeFunctionMulti,
	private val itemResults: List<Pair<Item, Int>> = listOf(),
	private val fluidResults: List<Pair<Fluid, Int>> = listOf()
) : RecipeBuilder {
	private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
	var time: Int = 0
	var energy: Int = 0
	override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
		this.criteria[name] = criterion
		return this
	}

	override fun group(groupName: String?): FluidEnergyBuilder = this
	fun timeRequired(time: Int): FluidEnergyBuilder = this.also { this.time = time }
	fun timeRequiredInSeconds(seconds: Int): FluidEnergyBuilder = this.also { this.time = seconds * 20 }
	fun energyRequired(energy: Int): FluidEnergyBuilder = this.also { this.energy = energy }
	private fun buildAdvancement(recipeOutput: RecipeOutput, id: ResourceLocation): AdvancementHolder {
		val advancement = recipeOutput.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(Builder.recipe(id))
			.requirements(Strategy.OR)
		return advancement.build(id.withPrefix("recipes/"))
	}

	var items: NonNullList<SizedIngredient> = NonNullList.create()
	var fluids: NonNullList<SizedFluidIngredient> = NonNullList.create()
	fun fluidRequired(fluid: Fluid, amount: Int = 1000): FluidEnergyBuilder =
		this.also { this.fluids.add(SizedFluidIngredient.of(fluid, amount)) }

	fun fluidRequired(tag: TagKey<Fluid>, amount: Int = 1000): FluidEnergyBuilder =
		this.also { this.fluids.add(SizedFluidIngredient.of(tag, amount)) }

	fun itemRequired(item: Item, count: Int = 1): FluidEnergyBuilder =
		this.also { this.items.add(SizedIngredient.of(item, count)) }

	fun itemRequired(item: DeferredItem<Item>, count: Int = 1): FluidEnergyBuilder =
		this.itemRequired(item.get(), count)

	fun itemRequired(tag: TagKey<Item>, count: Int = 1): FluidEnergyBuilder =
		this.also { this.items.add(SizedIngredient.of(tag, count)) }

	override fun getResult(): Item = this.itemResults[0].first
	override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
		require(this.items.isNotEmpty() || this.fluids.isNotEmpty()) { "Items or Fluids should have at least one ingredient!" }
		recipeOutput.accept(
			id,
			this.recipe.invoke(
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
			), this.buildAdvancement(recipeOutput, id)
		)
	}
}