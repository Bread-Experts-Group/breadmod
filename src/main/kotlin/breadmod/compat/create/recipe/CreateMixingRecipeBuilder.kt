package breadmod.compat.create.recipe

import breadmod.recipe.CountedIngredient
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
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.function.Consumer

@Suppress("unused")
class CreateMixingRecipeBuilder(private val results: CountedIngredient) : CraftingRecipeBuilder(), RecipeBuilder {
    constructor(result: ItemStack) : this(CountedIngredient(result))
    constructor(result: ItemLike, count: Int = 1) : this(ItemStack(result, count))

    private val advancement = Advancement.Builder.recipeAdvancement()
    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): CreateMixingRecipeBuilder = this.also {
        advancement.addCriterion(pCriterionName, pCriterionTrigger)
    }

    private var group: String? = null
    override fun group(pGroupName: String?): CreateMixingRecipeBuilder = this.also { it.group = pGroupName }

    private val itemRequirements = mutableListOf<ItemStack>()
    private var fluidRequirement: FluidIngredient? = null

    fun requires(itemIngredient: Ingredient) = this.also { itemRequirements.addAll(itemIngredient.items) }
    fun requires(itemStack: ItemStack) = this.also { itemRequirements.add(itemStack) }
    fun requires(item: ItemLike, count: Int = 1) = this.also { itemRequirements.add(ItemStack(item, count)) }

    fun requiresFluid(fluidIngredient: FluidIngredient) = this.also { fluidRequirement = fluidIngredient }
    fun requiresFluid(fluidStack: FluidStack) = this.also { fluidRequirement = FluidIngredient.fromFluidStack(fluidStack) }
    fun requiresFluid(fluid: Fluid, count: Int = 1) = this.also { fluidRequirement = FluidIngredient.fromFluid(fluid, count) }

    override fun getResult(): Item = results.items.first().item
    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(
            Result(
                pRecipeId,
                results,
                CountedIngredient(itemRequirements),
                fluidRequirement
            )
        )
    }

    private class Result(
        private val id: ResourceLocation,
        private val results: CountedIngredient,

        private val itemRequirements: CountedIngredient,
        private val fluidRequirement: FluidIngredient?,
    ) : FinishedRecipe {
        override fun serializeRecipeData(pJson: JsonObject) {
            pJson.add("ingredients", itemRequirements.toJson().also {
                if(fluidRequirement != null) it.add(fluidRequirement.serialize())
            })
            pJson.add("results", results.toJson())
        }

        override fun getType(): RecipeSerializer<*> = AllRecipeTypes.MIXING.getSerializer()

        override fun serializeAdvancement(): JsonObject? = null
        override fun getAdvancementId(): ResourceLocation? = null
        override fun getId(): ResourceLocation = id
    }
}