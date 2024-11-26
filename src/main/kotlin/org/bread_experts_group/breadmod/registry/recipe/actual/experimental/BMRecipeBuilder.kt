package org.bread_experts_group.breadmod.registry.recipe.actual.experimental

import net.minecraft.advancements.*
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation

abstract class BMRecipeBuilder : RecipeBuilder {
    val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    var time = 0
    var energy = 0

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): RecipeBuilder = this

    fun timeRequired(time: Int) = this.also { this.time = time }
    fun energyRequired(energy: Int) = this.also { this.energy = energy }

    protected fun buildAdvancement(recipeOutput: RecipeOutput, id: ResourceLocation): AdvancementHolder {
        val advancement = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        return advancement.build(id.withPrefix("recipes/"))
    }
}