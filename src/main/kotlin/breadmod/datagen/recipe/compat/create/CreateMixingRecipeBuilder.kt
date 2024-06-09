package breadmod.datagen.recipe.compat.create

import breadmod.datagen.recipe.IFluidBearingRecipeBuilder
import breadmod.datagen.recipe.IItemBearingRecipeBuilder
import breadmod.util.jsonifyFluidList
import breadmod.util.jsonifyItemList
import breadmod.util.jsonifyTagList
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.simibubi.create.AllRecipeTypes
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.CraftingRecipeBuilder
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.function.Consumer

@Suppress("unused")
class CreateMixingRecipeBuilder(
    private val itemResults: List<ItemStack> = listOf(),
    private val fluidResults: List<FluidStack> = listOf()
) : CraftingRecipeBuilder(), IItemBearingRecipeBuilder, IFluidBearingRecipeBuilder {
    constructor(result: ItemStack): this(itemResults = listOf(result))
    constructor(result: ItemLike, count: Int = 1): this(ItemStack(result, count))
    constructor(result: FluidStack): this(fluidResults = listOf(result))
    constructor(result: Fluid, count: Int = 1): this(FluidStack(result, count))
    constructor(itemResult: ItemStack, fluidResult: FluidStack): this(listOf(itemResult), listOf(fluidResult))
    constructor(itemResult: ItemStack, fluidsResult: List<FluidStack>): this(listOf(itemResult), fluidsResult)
    constructor(itemsResult: List<ItemStack>, fluidResult: FluidStack): this(itemsResult, listOf(fluidResult))

    private val advancement = Advancement.Builder.recipeAdvancement()
    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): CreateMixingRecipeBuilder = this
    override fun group(pGroupName: String?): CreateMixingRecipeBuilder = this

    enum class HeatRequirement {
        HEATED,
        SUPERHEATED
    }

    private var heatRequirement: String? = null
    fun heatRequirement(requirement: HeatRequirement) = this.also { heatRequirement = requirement.name.lowercase() }

    override val fluidsRequired: MutableList<FluidStack> = mutableListOf()
    override val fluidsRequiredTagged: MutableList<Pair<TagKey<Fluid>, Int>> = mutableListOf()
    override val itemsRequired: MutableList<ItemStack> = mutableListOf()
    override val itemsRequiredTagged: MutableList<Pair<TagKey<ItemLike>, Int>> = mutableListOf()

    override fun requiresFluid(fluidStack: FluidStack) = this.also { super.requiresFluid(fluidStack) }
    override fun requiresFluid(fluid: Fluid, count: Int) = this.also { super.requiresFluid(fluid, count) }
    override fun requiresFluid(fluid: TagKey<Fluid>, count: Int) = this.also { super.requiresFluid(fluid, count) }
    override fun requiresItem(itemStack: ItemStack) = this.also { super.requiresItem(itemStack) }
    override fun requiresItem(item: ItemLike, count: Int) = this.also { super.requiresItem(item, count) }
    override fun requiresItem(item: TagKey<ItemLike>, count: Int) = this.also { super.requiresItem(item, count) }

    override fun getResult(): Item = itemResults.first().item
    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(object : FinishedRecipe {
            override fun serializeRecipeData(pJson: JsonObject) {
                if(heatRequirement != null) pJson.addProperty("heatRequirement", heatRequirement)
                pJson.add("ingredients", JsonArray().also {
                    itemsRequired.jsonifyItemList(it, "item")
                    fluidsRequired.jsonifyFluidList(it, "fluid")
                    itemsRequiredTagged.jsonifyTagList(it, "itemTag")
                    fluidsRequiredTagged.jsonifyTagList(it, "fluidTag")
                })
                pJson.add("results", JsonArray().also {
                    itemResults.jsonifyItemList(it, "item")
                    fluidResults.jsonifyFluidList(it, "fluid")
                })
            }

            override fun getType(): RecipeSerializer<*> = AllRecipeTypes.MIXING.getSerializer()

            override fun serializeAdvancement(): JsonObject? = null
            override fun getAdvancementId(): ResourceLocation? = null
            override fun getId(): ResourceLocation = pRecipeId
        })
    }
}