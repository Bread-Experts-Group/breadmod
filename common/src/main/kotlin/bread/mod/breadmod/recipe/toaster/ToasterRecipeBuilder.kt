package bread.mod.breadmod.recipe.toaster

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

class ToasterRecipeBuilder(
    val input: ItemStack,
    val itemResult: ItemStack
) : RecipeBuilder {
    var group: String? = null
    val criteria: MutableMap<String, Criterion<*>> = hashMapOf()

    override fun unlockedBy(
        name: String,
        criterion: Criterion<*>
    ): RecipeBuilder {
        criteria.put(name, criterion)
        return this
    }

    override fun group(groupName: String?): RecipeBuilder {
        group = groupName
        return this
    }

    override fun getResult(): Item = itemResult.item

    override fun save(
        recipeOutput: RecipeOutput,
        id: ResourceLocation
    ) {
        val recipe = ToasterRecipe(
            input.copyWithCount(2),
            itemResult.copyWithCount(2)
        )
        val advancement: Advancement.Builder = recipeOutput.advancement()
            .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
            .rewards(AdvancementRewards.Builder.recipe(id))
            .requirements(AdvancementRequirements.Strategy.OR)
        criteria.forEach(advancement::addCriterion)
        recipeOutput.accept(id, recipe, advancement.build(id.withPrefix("recipes/")))
    }
}