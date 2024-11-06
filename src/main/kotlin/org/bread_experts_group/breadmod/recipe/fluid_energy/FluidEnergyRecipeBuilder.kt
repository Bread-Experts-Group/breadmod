package org.bread_experts_group.breadmod.recipe.fluid_energy

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRequirements
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class FluidEnergyRecipeBuilder(
    private val results: List<Pair<ItemStack, Int>> = listOf()
) : RecipeBuilder {
    private val criteria: MutableMap<String, Criterion<*>> = hashMapOf()
    private var items = mutableListOf<ItemStack>()
    private var itemsTagged = mutableListOf<TagKey<Item>>()

    override fun unlockedBy(name: String, criterion: Criterion<*>): RecipeBuilder = this
    override fun group(groupName: String?): RecipeBuilder = this
    override fun getResult(): Item = results.first().first.item

    fun requiresItem(item: ItemStack) = this.also { items.add(item) }
    fun requiresItemTagged(tagKey: TagKey<Item>) = this.also { itemsTagged.add(tagKey) }

    override fun save(recipeOutput: RecipeOutput, id: ResourceLocation) {
        val outputList = mutableListOf<ItemStack>()
        results.forEach { outputList.add(it.first.copyWithCount(it.second)) }
        val recipe = FluidEnergyRecipe(items, itemsTagged, outputList)
        val advancement: Advancement.Builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        criteria.forEach(advancement::addCriterion)
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")))
    }
}