package org.bread_experts_group.breadmod.experimental.recipe

import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation

@Suppress("unused")
abstract class BMRecipeBuilder : RecipeBuilder {
    val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    var time: Int = 0
    var energy: Int = 0

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder {
        criteria[name] = criterion
        return this
    }

    override fun group(groupName: String?): RecipeBuilder = this

    fun timeRequired(time: Int): BMRecipeBuilder = this.also { this.time = time }
    fun energyRequired(energy: Int): BMRecipeBuilder = this.also { this.energy = energy }

    protected fun buildAdvancement(recipeOutput: RecipeOutput, id: ResourceLocation): AdvancementHolder {
        val advancement = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        return advancement.build(id.withPrefix("recipes/"))
    }
}