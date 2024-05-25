package breadmod.datagen.recipe

import breadmod.recipe.serializers.SimpleItemEnergyRecipeSerializer
import breadmod.registry.recipe.ModRecipeSerializers
import com.google.gson.JsonObject
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import java.util.function.Consumer

@Suppress("unused")
class WheatCrusherRecipeBuilder(
    val itemResults: List<ItemStack> = listOf(),
): ItemBearingRecipeBuilder, TimedRecipeBuilder, PoweredRecipeBuilder {
    constructor(result: ItemStack): this(itemResults = listOf(result))
    constructor(result: ItemLike, count: Int = 1): this(ItemStack(result, count))

    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): RecipeBuilder = this
    override fun group(pGroupName: String?): RecipeBuilder = this
    override fun getResult(): Item = itemResults.first().item

    override val itemsRequired: MutableList<ItemStack> = mutableListOf()
    override val itemsRequiredTagged: MutableList<Pair<TagKey<ItemLike>, Int>> = mutableListOf()

    override fun requiresItem(itemStack: ItemStack) = this.also { super.requiresItem(itemStack) }
    override fun requiresItem(item: ItemLike, count: Int) = this.also { super.requiresItem(item, count) }
    override fun requiresItem(item: TagKey<ItemLike>, count: Int) = this.also { super.requiresItem(item, count) }

    override var powerInRF: Int = 0
    override var timeInTicks: Int = 0
    override fun setRFRequired(rf: Int) = this.also { super.setRFRequired(rf) }
    override fun setTimeRequired(ticks: Int) = this.also { super.setTimeRequired(ticks) }

    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(object: FinishedRecipe {
            override fun serializeRecipeData(pJson: JsonObject) {
                type.toJson(
                    pJson, pRecipeId,
                    timeInTicks, powerInRF.takeIf { it > 0 },
                    itemsRequired.takeIf { it.isNotEmpty() },
                    itemsRequiredTagged.takeIf { it.isNotEmpty() },
                    itemResults.takeIf { it.isNotEmpty() }
                )
            }

            override fun getId(): ResourceLocation = pRecipeId
            override fun getType(): SimpleItemEnergyRecipeSerializer<*> = ModRecipeSerializers.WHEAT_CRUSHING.get()

            override fun serializeAdvancement(): JsonObject? = null
            override fun getAdvancementId(): ResourceLocation? = null
        })
    }
}