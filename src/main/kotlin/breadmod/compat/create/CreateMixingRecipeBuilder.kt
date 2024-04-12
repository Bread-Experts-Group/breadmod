package breadmod.compat.create

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.simibubi.create.AllRecipeTypes
import com.simibubi.create.foundation.fluid.FluidIngredient
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.CraftingRecipeBuilder
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

class CreateMixingRecipeBuilder(
    val inputs: List<Ingredient> = listOf(),
    val fluidInputs: List<FluidIngredient> = listOf(),
    val result: ItemLike,
    val count: Int = 1,
) : CraftingRecipeBuilder(), RecipeBuilder {
    private val advancement: Advancement.Builder = Advancement.Builder.recipeAdvancement()
    private var group: String? = null

    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): CreateMixingRecipeBuilder {
        advancement.addCriterion(pCriterionName, pCriterionTrigger)
        return this
    }

    override fun group(pGroupName: String?): CreateMixingRecipeBuilder {
        this.group = pGroupName
        return this
    }

    override fun getResult(): Item = this.result.asItem()
    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(
            Result(
                pRecipeId,
                result,
                count,
                inputs,
                fluidInputs
            )
        )
    }

    private class Result(
        private val id: ResourceLocation,
        private val result: ItemLike,
        private val count: Int,
        private val ingredients: List<Ingredient>,
        private val fluidIngredients: List<FluidIngredient>,
    ) : FinishedRecipe {
        override fun serializeRecipeData(pJson: JsonObject) {
            val jsonArray = JsonArray()

            for (ingredient in this.ingredients) jsonArray.add(ingredient.toJson())
            for (ingredient in this.fluidIngredients) jsonArray.add(ingredient.serialize())

            pJson.add("ingredients", jsonArray)
            val jsonObject = JsonObject()
            jsonObject.addProperty("item", ForgeRegistries.ITEMS.getKey(result.asItem()).toString())
            if (this.count > 1) jsonObject.addProperty("count", this.count)

            val resultArray = JsonArray()
            pJson.add("results", resultArray)
            resultArray.add(jsonObject)
        }

        override fun getType(): RecipeSerializer<*> = AllRecipeTypes.MIXING.getSerializer()

        override fun serializeAdvancement(): JsonObject? = null
        override fun getAdvancementId(): ResourceLocation? = null
        override fun getId(): ResourceLocation = id
    }
}
