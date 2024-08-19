package breadmod.recipe.serializer

import breadmod.recipe.fluidEnergy.FluidEnergyRecipe
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
    private companion object {
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
        val outputs = p1.getAsJsonObject(OUTPUT_KEY)
        return factory(
            ResourceLocation(p1.get(ENTRY_ID).asString),
            p1.get(TIME_KEY).asInt,
            inputs.get(ENERGY_KEY)?.asInt,
            requiredFluids?.getAsJsonArray(CERTAIN_KEY)?.extractJsonFluidList(),
            requiredFluids?.getAsJsonArray(TAGGED_KEY)?.extractJsonTagList(ForgeRegistries.FLUIDS),
            requiredItems?.getAsJsonArray(CERTAIN_KEY)?.extractJsonItemList(),
            requiredItems?.getAsJsonArray(TAGGED_KEY)?.extractJsonTagList(ForgeRegistries.ITEMS),
            outputs?.getAsJsonArray(FLUIDS_KEY)?.extractJsonFluidList(),
            outputs?.getAsJsonArray(ITEMS_KEY)?.extractJsonItemList()
        )
    }

    fun <T: ItemLike> toJson(
        to: JsonObject, id: ResourceLocation, time: Int, energy: Int?,
        fluidsRequired: List<FluidStack>?, fluidsRequiredTagged: List<Pair<TagKey<Fluid>, Int>>?,
        itemsRequired: List<ItemStack>?, itemsRequiredTagged: List<Pair<TagKey<T>, Int>>?,
        itemsOutput: List<ItemStack>?, fluidsOutput: List<FluidStack>?
    ) = to.also {
        it.addProperty(ENTRY_ID, id.toString())
        it.addProperty(TIME_KEY, time)
        it.add(INPUT_KEY, JsonObject().also { required ->
            if(energy != null) required.addProperty(ENERGY_KEY, energy)
            JsonObject().also { obj ->
                if(!fluidsRequired.isNullOrEmpty()) obj.add(CERTAIN_KEY, fluidsRequired.jsonifyFluidList())
                if(!fluidsRequiredTagged.isNullOrEmpty()) obj.add(TAGGED_KEY, fluidsRequiredTagged.jsonifyTagList())
                if(obj.size() > 0) required.add(FLUIDS_KEY, obj)
            }
            JsonObject().also { obj ->
                if(!itemsRequired.isNullOrEmpty()) obj.add(CERTAIN_KEY, itemsRequired.jsonifyItemList())
                if(!itemsRequiredTagged.isNullOrEmpty()) obj.add(TAGGED_KEY, itemsRequiredTagged.jsonifyTagList())
                if(obj.size() > 0) required.add(ITEMS_KEY, obj)
            }
            if(required.size() == 0) throw IllegalArgumentException("Not enough inputs")
        })
        JsonObject().also { outputs ->
            if (!fluidsOutput.isNullOrEmpty()) outputs.add(FLUIDS_KEY, fluidsOutput.jsonifyFluidList())
            if (!itemsOutput.isNullOrEmpty()) outputs.add(ITEMS_KEY, itemsOutput.jsonifyItemList())
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