package breadmod.datagen.recipe

import breadmod.recipe.AbstractFluidEnergyRecipe.Companion.dsbuyn
import breadmod.recipe.serializers.SimpleFluidEnergyRecipeSerializer
import com.google.gson.JsonObject
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.fluids.FluidStack
import java.util.function.Consumer

class FluidEnergyRecipeBuilder(val itemOutputs: List<ItemStack>, val fluidOutputs: List<FluidStack> = listOf()): RecipeBuilder {
    // TODO.
    override fun unlockedBy(pCriterionName: String, pCriterionTrigger: CriterionTriggerInstance): RecipeBuilder = this
    override fun group(pGroupName: String?): RecipeBuilder = this
    override fun getResult(): Item = dsbuyn()

    lateinit var serializer: SimpleFluidEnergyRecipeSerializer<*>
    fun setSerializer(setSerializer: SimpleFluidEnergyRecipeSerializer<*>) = this.also { serializer = setSerializer }
    var energy = 0
    var time = 0
    var fluids = mutableListOf<FluidStack>()
    var items = mutableListOf<ItemStack>()
    fun addItem(itemStack: ItemStack) = this.also { items.add(itemStack) }
    fun addFluid(fluidStack: FluidStack) = this.also { fluids.add(fluidStack) }
    fun setEnergy(energy: Int) = this.also { it.energy = energy }
    fun setTimeTicks(ticks: Int) = this.also { it.time = ticks }

    override fun save(pFinishedRecipeConsumer: Consumer<FinishedRecipe>, pRecipeId: ResourceLocation) {
        pFinishedRecipeConsumer.accept(object: FinishedRecipe {
            override fun serializeRecipeData(pJson: JsonObject) {
                serializer.toJson(pJson, pRecipeId, time, energy, fluids, items, fluidOutputs, itemOutputs)
            }

            override fun getId(): ResourceLocation = pRecipeId
            override fun getType(): RecipeSerializer<*> = serializer

            override fun serializeAdvancement(): JsonObject? = null
            override fun getAdvancementId(): ResourceLocation? = null
        })
    }
}