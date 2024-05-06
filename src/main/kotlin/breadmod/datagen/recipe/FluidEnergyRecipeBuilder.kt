package breadmod.datagen.recipe

import breadmod.recipe.serializers.SimpleFluidEnergyRecipeSerializer
import com.google.gson.JsonObject
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import java.util.function.Consumer

class FluidEnergyRecipeBuilder(
    val itemOutputs: List<ItemStack>,
    val fluidOutputs: List<FluidStack> = listOf()
): ItemBearingRecipeBuilder, FluidBearingRecipeBuilder, TimedRecipeBuilder, PoweredRecipeBuilder {
    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): RecipeBuilder = this
    override fun group(pGroupName: String?): RecipeBuilder = this
    override fun getResult(): Item = itemOutputs.first().item

    lateinit var serializer: SimpleFluidEnergyRecipeSerializer<*>
    fun setSerializer(setSerializer: SimpleFluidEnergyRecipeSerializer<*>) = this.also { serializer = setSerializer }

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

    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(object: FinishedRecipe {
            override fun serializeRecipeData(pJson: JsonObject) {
                serializer.toJson(
                    pJson, pRecipeId,
                    timeInTicks, powerInRF,
                    fluidsRequired, fluidsRequiredTagged,
                    itemsRequired, itemsRequiredTagged,
                    fluidOutputs, itemOutputs
                )
            }

            override fun getId(): ResourceLocation = pRecipeId
            override fun getType(): RecipeSerializer<*> = serializer

            override fun serializeAdvancement(): JsonObject? = null
            override fun getAdvancementId(): ResourceLocation? = null
        })
    }
}