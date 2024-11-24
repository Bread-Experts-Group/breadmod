package org.bread_experts_group.breadmod.registry.recipe.actual.dough_machine

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.neoforged.neoforge.fluids.FluidStack

class DoughMachineRecipeBuilder(
    private val result: ItemStack,
    private val count: Int,
    private val fResult: FluidStack = FluidStack.EMPTY,
    private val amount: Int = 0
) : RecipeBuilder {
    private var group: String? = null
    private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    private var time = 0
    private var energy = 0
    private var item = ItemStack.EMPTY
    private var fluid = FluidStack.EMPTY

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
        criteria[name] = criterion
        return this
    }

    fun itemRequired(item: Item, count: Int = 1) = this.also { this.item = ItemStack(item, count) }
    fun fluidRequired(fluid: Fluid, amount: Int = 1000) = this.also { this.fluid = FluidStack(fluid, amount) }
    fun timeRequired(time: Int) = this.also { this.time = time }
    fun energyRequired(energy: Int) = this.also { this.energy = energy }

    override fun group(groupName: String?): RecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item = result.item

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val recipe = DoughMachineRecipe(
            item.copy(),
            result.copyWithCount(count),
            fluid.copy(),
            fResult.copyWithAmount(amount),
            energy,
            time
        )
        val advancement: Advancement.Builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        criteria.forEach(advancement::addCriterion)
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")))
    }
}