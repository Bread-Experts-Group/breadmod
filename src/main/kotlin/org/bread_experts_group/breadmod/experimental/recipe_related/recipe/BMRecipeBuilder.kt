package org.bread_experts_group.breadmod.experimental.recipe_related.recipe

import net.minecraft.advancements.AdvancementHolder
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
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient

@Suppress("unused")
abstract class BMRecipeBuilder : RecipeBuilder {
    private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    var time: Int = 0
    var energy: Int = 0

    fun emptyFluidIngredient(): SizedFluidIngredient = SizedFluidIngredient.of(Fluids.WATER, 1)
    fun emptyItemIngredient(): SizedIngredient = SizedIngredient.of(Items.AIR, 1)

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

    abstract class Single : BMRecipeBuilder() {
        var fluid = emptyFluidIngredient()
        var item = emptyItemIngredient()

        fun fluidRequired(fluid: Fluid, amount: Int = 1000) =
            this.also { this.fluid = SizedFluidIngredient.of(fluid, amount) }

        fun fluidRequired(tag: TagKey<Fluid>, amount: Int = 1000) =
            this.also { fluid = SizedFluidIngredient.of(tag, amount) }

        fun itemRequired(item: Item, count: Int = 1) = this.also { this.item = SizedIngredient.of(item, count) }
        fun itemRequired(tag: TagKey<Item>, count: Int = 1) =
            this.also { item = SizedIngredient.of(tag, count) }
    }

    abstract class Multi : BMRecipeBuilder() {
        var items = NonNullList.create<SizedIngredient>()
        var fluids = NonNullList.create<SizedFluidIngredient>()

        fun fluidRequired(fluid: Fluid, amount: Int = 1000): Multi =
            this.also { this.fluids.add(SizedFluidIngredient.of(fluid, amount)) }

        fun fluidRequired(tag: TagKey<Fluid>, amount: Int = 1000): Multi =
            this.also { this.fluids.add(SizedFluidIngredient.of(tag, amount)) }

        fun itemRequired(item: Item, count: Int = 1): Multi =
            this.also { this.items.add(SizedIngredient.of(item, count)) }

        fun itemRequired(tag: TagKey<Item>, count: Int = 1): Multi =
            this.also { this.items.add(SizedIngredient.of(tag, count)) }
    }
}