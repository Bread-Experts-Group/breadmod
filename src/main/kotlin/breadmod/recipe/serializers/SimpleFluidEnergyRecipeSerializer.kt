package breadmod.recipe.serializers

import breadmod.recipe.AbstractFluidEnergyRecipe
import breadmod.util.*
import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries

@Suppress("MemberVisibilityCanBePrivate")
class SimpleFluidEnergyRecipeSerializer<T: AbstractFluidEnergyRecipe>(
    val factory: (
        cId: ResourceLocation, cTime: Int, cEnergy: Int,
        cFluidsRequired: List<FluidStack>, cFluidTagsRequired: List<Pair<TagKey<Fluid>, Int>>,
        cItemsRequired: List<ItemStack>, cItemTagsRequired: List<Pair<TagKey<Item>, Int>>
    ) -> T
) : RecipeSerializer<T> {
    companion object {
        const val ENERGY_KEY = "energy"
        const val TIME_KEY = "time"
        const val FLUIDS_KEY = "fluids"
        const val ITEMS_KEY = "items"

        const val INPUT_KEY = "inputs"
        const val CERTAIN_KEY = "certain"
        const val TAGGED_KEY = "tags"
    }

    override fun fromJson(p0: ResourceLocation, p1: JsonObject): T {
        val inputs = p1.getAsJsonObject(INPUT_KEY)
        val requiredFluids = inputs.getAsJsonObject(FLUIDS_KEY)
        val requiredItems = inputs.getAsJsonObject(ITEMS_KEY)
        return factory(
            ResourceLocation(p1.get(ENTRY_ID_KEY).asString),
            p1.get(TIME_KEY).asInt,
            inputs.get(ENERGY_KEY).asInt,
            requiredFluids.getAsJsonArray(CERTAIN_KEY).extractJsonFluidList(),
            requiredFluids.getAsJsonArray(TAGGED_KEY).extractJsonTagList(ForgeRegistries.FLUIDS),
            requiredItems.getAsJsonArray(CERTAIN_KEY).extractJsonItemList(),
            requiredItems.getAsJsonArray(TAGGED_KEY).extractJsonTagList(ForgeRegistries.ITEMS)
        )
    }

    fun toJson(
        to: JsonObject, location: ResourceLocation,
        time: Int, energy: Int,
        fluidList: List<FluidStack>, fluidTagList: List<Pair<TagKey<Fluid>, Int>>,
        itemList: List<ItemStack>, itemTagList: List<Pair<TagKey<ItemLike>, Int>>
    ) = to.also {
        it.addProperty(ENTRY_ID_KEY, location.toString())
        it.addProperty(TIME_KEY, time)
        it.add(INPUT_KEY, JsonObject().also { required ->
            required.addProperty(ENERGY_KEY, energy)
            required.add(FLUIDS_KEY, JsonObject().also { obj ->
                obj.add(CERTAIN_KEY, fluidList.jsonifyFluidList())
                obj.add(TAGGED_KEY, fluidTagList.jsonifyTagList())
            })
            required.add(ITEMS_KEY, JsonObject().also { obj ->
                obj.add(CERTAIN_KEY, itemList.jsonifyItemList())
                obj.add(TAGGED_KEY, itemTagList.jsonifyTagList())
            })
        })
    }

    override fun fromNetwork(p0: ResourceLocation, p1: FriendlyByteBuf): T =
        factory(
            ResourceLocation(p1.readUtf()),
            p1.readInt(), p1.readInt(),
            p1.readFluidList(), p1.readTagList(ForgeRegistries.FLUIDS),
            p1.readItemList(), p1.readTagList(ForgeRegistries.ITEMS)
        )

    override fun toNetwork(pBuffer: FriendlyByteBuf, pRecipe: T) {
        pBuffer.writeUtf(pRecipe.id.toString())
        pBuffer.writeInt(pRecipe.time)
        pBuffer.writeInt(pRecipe.energy)
        pBuffer.writeFluidList(pRecipe.fluidsRequired)
        pBuffer.writeTagList(ForgeRegistries.FLUIDS, pRecipe.fluidsRequiredTagged)
        pBuffer.writeItemList(pRecipe.itemsRequired)
        pBuffer.writeTagList(ForgeRegistries.ITEMS, pRecipe.itemsRequiredTagged)
    }
}