package breadmod.compat.create

import com.google.common.collect.Lists
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.simibubi.create.AllRecipeTypes
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.CraftingRecipeBuilder
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import net.minecraftforge.registries.ForgeRegistries
import java.util.function.Consumer

@Suppress("unused")
class CreateMixingRecipeBuilder(pResult: ItemLike, private val count: Int) :
    CraftingRecipeBuilder(), RecipeBuilder {
    private val result: Item = pResult.asItem()
    private val ingredients: MutableList<Ingredient> = Lists.newArrayList()
    private val advancement: Advancement.Builder = Advancement.Builder.recipeAdvancement()
    private var group: String? = null

    fun requires(pTag: TagKey<Item?>): CreateMixingRecipeBuilder {
        return this.requires(Ingredient.of(pTag))
    }

    @JvmOverloads
    fun requires(pItem: ItemLike?, pQuantity: Int = 1): CreateMixingRecipeBuilder {
        for (i in 0 until pQuantity) {
            this.requires(Ingredient.of(pItem))
        }

        return this
    }

    @JvmOverloads
    fun requires(pIngredient: Ingredient, pQuantity: Int = 1): CreateMixingRecipeBuilder {
        for (i in 0 until pQuantity) {
            ingredients.add(pIngredient)
        }

        return this
    }

    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): CreateMixingRecipeBuilder {
        advancement.addCriterion(pCriterionName, pCriterionTrigger)
        return this
    }

    override fun group(pGroupName: String?): CreateMixingRecipeBuilder {
        this.group = pGroupName
        return this
    }

    override fun getResult(): Item {
        return this.result
    }

    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(
            Result(
                pRecipeId,
                this.result,
                this.count,
                this.ingredients
            )
        )
    }

    private class Result(
        private val id: ResourceLocation,
        private val result: Item,
        private val count: Int,
        private val ingredients: List<Ingredient>
    ) : ModFinishedRecipe() {
        override fun serializeRecipeData(pJson: JsonObject) {
            super.serializeRecipeData(pJson)

            val jsonArray = JsonArray()

            for (ingredient in this.ingredients) {
                jsonArray.add(ingredient.toJson())
            }

            pJson.add("ingredients", jsonArray)
            val jsonObject = JsonObject()
            jsonObject.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString())
            if (this.count > 1) {
                jsonObject.addProperty("count", this.count)
            }

            pJson.add("results", jsonObject)
        }

        override fun getType(): RecipeSerializer<*> {
            return AllRecipeTypes.MIXING.getSerializer()
        }

        override fun getId(): ResourceLocation {
            return this.id
        }
    }

    companion object {
        /**
         * Creates a new builder for a shapeless recipe.
         */
        fun mixing(pResult: ItemLike): CreateMixingRecipeBuilder {
            return CreateMixingRecipeBuilder(pResult, 1)
        }

        /**
         * Creates a new builder for a shapeless recipe.
         */
        fun mixing(pResult: ItemLike, pCount: Int): CreateMixingRecipeBuilder {
            return CreateMixingRecipeBuilder(pResult, pCount)
        }
    }
}
