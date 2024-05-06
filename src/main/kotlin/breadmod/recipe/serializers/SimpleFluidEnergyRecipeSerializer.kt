package breadmod.recipe.serializers

import breadmod.recipe.FluidEnergyRecipe
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
class SimpleFluidEnergyRecipeSerializer<T: FluidEnergyRecipe>(
    val factory: (
        cId: ResourceLocation, cTime: Int, cEnergy: Int?,
        cFluidsRequired: List<FluidStack>?, cFluidTagsRequired: List<Pair<TagKey<Fluid>, Int>>?,
        cItemsRequired: List<ItemStack>?, cItemTagsRequired: List<Pair<TagKey<Item>, Int>>?,
        cFluidsOutput: List<FluidStack>?, cItemsOutput: List<ItemStack>?
    ) -> T
) : RecipeSerializer<T> {
    companion object {
        const val ENERGY_KEY = "energy"
        const val TIME_KEY = "time"
        const val FLUIDS_KEY = "fluids"
        const val ITEMS_KEY = "items"

        const val OUTPUT_KEY = "outputs"
        const val INPUT_KEY = "inputs"
        const val CERTAIN_KEY = "certain"
        const val TAGGED_KEY = "tags"
    }

    override fun fromJson(p0: ResourceLocation, p1: JsonObject): T {
        val inputs = p1.getAsJsonObject(INPUT_KEY)
        val requiredFluids = inputs.getAsJsonObject(FLUIDS_KEY)
        val requiredItems = inputs.getAsJsonObject(ITEMS_KEY)
        val outputItems = p1.getAsJsonObject(OUTPUT_KEY)
        return factory(
            ResourceLocation(p1.get(ENTRY_ID_KEY).asString),
            p1.get(TIME_KEY).asInt,
            inputs.get(ENERGY_KEY).asInt,
            requiredFluids.getAsJsonArray(CERTAIN_KEY).extractJsonFluidList(),
            requiredFluids.getAsJsonArray(TAGGED_KEY).extractJsonTagList(ForgeRegistries.FLUIDS),
            requiredItems.getAsJsonArray(CERTAIN_KEY).extractJsonItemList(),
            requiredItems.getAsJsonArray(TAGGED_KEY).extractJsonTagList(ForgeRegistries.ITEMS),
            outputItems.getAsJsonArray(FLUIDS_KEY).extractJsonFluidList(),
            outputItems.getAsJsonArray(ITEMS_KEY).extractJsonItemList()
        )
    }

    fun toJson(
        to: JsonObject, location: ResourceLocation,
        time: Int, energy: Int?,
        fluidList: List<FluidStack>?, fluidTagList: List<Pair<TagKey<Fluid>, Int>>?,
        itemList: List<ItemStack>?, itemTagList: List<Pair<TagKey<ItemLike>, Int>>?,
        fluidOutputs: List<FluidStack>?, itemOutputs: List<ItemStack>?
    ) = to.also {
        it.addProperty(ENTRY_ID_KEY, location.toString())
        it.addProperty(TIME_KEY, time)
        it.add(INPUT_KEY, JsonObject().also { required ->
            if(energy != null) required.addProperty(ENERGY_KEY, energy)
            JsonObject().also { obj ->
                if(!fluidList.isNullOrEmpty()) obj.add(CERTAIN_KEY, fluidList.jsonifyFluidList())
                if(!fluidTagList.isNullOrEmpty()) obj.add(TAGGED_KEY, fluidTagList.jsonifyTagList())
                if(obj.size() > 0) required.add(FLUIDS_KEY, obj)
            }
            JsonObject().also { obj ->
                if(!itemList.isNullOrEmpty()) obj.add(CERTAIN_KEY, itemList.jsonifyItemList())
                if(!itemTagList.isNullOrEmpty()) obj.add(TAGGED_KEY, itemTagList.jsonifyTagList())
                if(obj.size() > 0) required.add(ITEMS_KEY, obj)
            }
            if(required.size() == 0) throw IllegalArgumentException("Not enough inputs")
        })
        JsonObject().also { outputs ->
            if (!fluidOutputs.isNullOrEmpty()) outputs.add(FLUIDS_KEY, fluidOutputs.jsonifyFluidList())
            if (!itemOutputs.isNullOrEmpty()) outputs.add(ITEMS_KEY, itemOutputs.jsonifyItemList())
            if (outputs.size() > 0) it.add(OUTPUT_KEY, outputs)
        }
    }

    override fun fromNetwork(p0: ResourceLocation, p1: FriendlyByteBuf): T =
        factory(
            ResourceLocation(p1.readUtf()),
            p1.readInt(),
            p1.readNullable { p1.readInt() },
            p1.readNullable { p1.readFluidList() }, p1.readNullable { p1.readTagList(ForgeRegistries.FLUIDS) },
            p1.readNullable { p1.readItemList () }, p1.readNullable { p1.readTagList(ForgeRegistries.ITEMS) },
            p1.readNullable { p1.readFluidList() }, p1.readNullable { p1.readItemList() }
        )

    override fun toNetwork(pBuffer: FriendlyByteBuf, pRecipe: T) {
        pBuffer.writeUtf(pRecipe.id.toString())
        pBuffer.writeInt(pRecipe.time)
        pBuffer.writeNullable(pRecipe.energy) { buf, int -> buf.writeInt(int) }
        pBuffer.writeNullable(pRecipe.fluidsRequired) { buf, stack -> buf.writeFluidList(stack) }
        pBuffer.writeNullable(pRecipe.fluidsRequiredTagged) { buf, tags -> buf.writeTagList(tags) }
        pBuffer.writeNullable(pRecipe.itemsRequired) { buf, stack -> buf.writeItemList(stack) }
        pBuffer.writeNullable(pRecipe.itemsRequiredTagged) { buf, tags -> buf.writeTagList(tags) }
        pBuffer.writeNullable(pRecipe.fluidsOutput) { buf, stack -> buf.writeFluidList(stack) }
        pBuffer.writeNullable(pRecipe.itemsOutput) { buf, stack -> buf.writeItemList(stack) }
    }
}