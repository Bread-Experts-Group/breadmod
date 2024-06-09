package breadmod.datagen.recipe

import breadmod.recipe.serializer.SimpleFluidEnergyRecipeSerializer
import com.google.gson.JsonObject
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.function.Consumer

@Suppress("unused")
class FluidEnergyRecipeBuilder(
    val itemResults: List<ItemStack> = listOf(),
    val fluidResults: List<FluidStack> = listOf()
): IItemBearingRecipeBuilder, IFluidBearingRecipeBuilder, ITimedRecipeBuilder, IPoweredRecipeBuilder {
    constructor(result: ItemStack): this(itemResults = listOf(result))
    constructor(result: ItemLike, count: Int = 1): this(ItemStack(result, count))
    constructor(result: FluidStack): this(fluidResults = listOf(result))
    constructor(result: Fluid, count: Int = 1): this(FluidStack(result, count))
    constructor(itemResult: ItemStack, fluidResult: FluidStack): this(listOf(itemResult), listOf(fluidResult))
    constructor(itemResult: ItemStack, fluidsResult: List<FluidStack>): this(listOf(itemResult), fluidsResult)
    constructor(itemsResult: List<ItemStack>, fluidResult: FluidStack): this(itemsResult, listOf(fluidResult))

    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): RecipeBuilder = this
    override fun group(pGroupName: String?): RecipeBuilder = this
    override fun getResult(): Item = itemResults.first().item

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

    override var powerInRF: Int = 0
    override var timeInTicks: Int = 0
    override fun setRFRequired(rf: Int) = this.also { super.setRFRequired(rf) }
    override fun setTimeRequired(ticks: Int) = this.also { super.setTimeRequired(ticks) }

    private var serializer: SimpleFluidEnergyRecipeSerializer<*>? = null
    fun setSerializer(newSerializer: SimpleFluidEnergyRecipeSerializer<*>) = this.also { serializer = newSerializer }

    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(object: FinishedRecipe {
            override fun serializeRecipeData(pJson: JsonObject) {
                type.toJson(
                    pJson, pRecipeId,
                    timeInTicks, powerInRF.takeIf { it != 0 },
                    fluidsRequired.takeIf { it.isNotEmpty() }, fluidsRequiredTagged.takeIf { it.isNotEmpty() },
                    itemsRequired.takeIf { it.isNotEmpty() }, itemsRequiredTagged.takeIf { it.isNotEmpty() },
                    fluidResults.takeIf { it.isNotEmpty() }, itemResults.takeIf { it.isNotEmpty() }
                )
            }

            override fun getId(): ResourceLocation = pRecipeId
            override fun getType(): SimpleFluidEnergyRecipeSerializer<*> = serializer ?: throw IllegalArgumentException("You must provide a serializer.")

            override fun serializeAdvancement(): JsonObject? = null
            override fun getAdvancementId(): ResourceLocation? = null
        })
    }
}