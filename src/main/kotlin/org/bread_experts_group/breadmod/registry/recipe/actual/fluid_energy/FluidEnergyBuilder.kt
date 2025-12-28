package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements.Strategy
import net.minecraft.advancements.AdvancementRewards.Builder
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentType
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.material.Fluid
import java.math.BigDecimal

// todo probably introduce nullability on all the methods so the recipe provider can use null inputs for recipe building
class FluidEnergyBuilder(
	private val recipe: RecipeFunctionMulti,
	private val itemResults: MutableList<BigDescriptor<Item>> = mutableListOf(),
	private val fluidResults: MutableList<BigDescriptor<Fluid>> = mutableListOf(),
) : RecipeBuilder {
	private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
	var time: ULong = 0u
	var energy: BigDecimal? = null
	override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
		this.criteria[name] = criterion
		return this
	}

	private fun invokeRecipe(): FluidEnergyRecipe = this.recipe.invoke(
		this.items,
		this.itemResults,
		this.fluids,
		this.fluidResults,
		this.time,
		this.energy
	)

	override fun group(groupName: String?): FluidEnergyBuilder = this
	fun timeRequired(time: Long): FluidEnergyBuilder = this.also { this.time = time.toULong() }
	fun timeRequiredInSeconds(seconds: ULong): FluidEnergyBuilder = this.also { this.time = seconds * 20uL }
	fun energyRequired(energy: BigDecimal): FluidEnergyBuilder = this.also { this.energy = energy }
	fun energyRequired(energy: Int): FluidEnergyBuilder = this.energyRequired(BigDecimal(energy))
	private fun RecipeOutput.buildAdvancement(id: ResourceLocation): AdvancementHolder {
		val advancement = this.advancement()
			.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
			.rewards(Builder.recipe(id))
			.requirements(Strategy.OR)
		return advancement.build(id.withPrefix("recipes/"))
	}

	var items: MutableList<InputOption<Item>> = mutableListOf()
	var fluids: MutableList<InputOption<Fluid>> = mutableListOf()
	fun fluidRequired(fluid: Fluid, amount: BigDecimal): BuilderEntry<Fluid> = BuilderEntry(fluid, amount)
	fun fluidRequired(fluid: Fluid, amount: Int): BuilderEntry<Fluid> = this.fluidRequired(fluid, BigDecimal(amount))
	fun fluidRequired(bundle: Pair<Fluid, Int>): BuilderEntry<Fluid> = this.fluidRequired(bundle.first, bundle.second)

	fun fluidRequired(tag: TagKey<Fluid>, amount: Int): FluidEnergyBuilder = this.also {
		this.fluids.add(InputOption.tag(Fluid::class, tag to BigDecimal(amount)))
	}

	fun itemRequired(item: Item, amount: BigDecimal): BuilderEntry<Item> = BuilderEntry(item, amount)
	fun itemRequired(item: Item, amount: Int): BuilderEntry<Item> = this.itemRequired(item, BigDecimal(amount))
	fun itemRequired(bundle: Pair<Item, Int>): BuilderEntry<Item> = this.itemRequired(bundle.first, bundle.second)

	fun itemRequired(tag: TagKey<Item>, amount: Int): FluidEnergyBuilder = this.also {
		this.items.add(InputOption.tag(Item::class, tag to BigDecimal(amount)))
	}

	override fun getResult(): Item = this.itemResults[0].value
	override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
		require(this.items.isNotEmpty() || this.fluids.isNotEmpty()) { "Items or Fluids should have at least one ingredient!" }
		recipeOutput.accept(id, this.invokeRecipe(), recipeOutput.buildAdvancement(id))
	}

	inner class BuilderEntry<E>(
		val value: E,
		val amount: BigDecimal,
		var components: DataComponentMap = DataComponentMap.EMPTY
	) {
		fun <V> withComponents(vararg components: Pair<DataComponentType<V>, V>): BuilderEntry<E> = this.also {
			val mapBuilder = DataComponentMap.builder()
			for ((component, value) in components) {
				mapBuilder.set(component, value)
			}
			this.components = mapBuilder.build()
		}

		fun finish(): FluidEnergyBuilder = this@FluidEnergyBuilder.also {
			val isItem = this.value as? Item
			if (isItem != null) it.items.add(
				InputOption.bigDescriptor(
					Item::class,
					BigDescriptor(this.amount, this.value as Item, this.components)
				)
			) else it.fluids.add(
				InputOption.bigDescriptor(
					Fluid::class,
					BigDescriptor(this.amount, this.value as Fluid, this.components)
				)
			)
		}
	}
}