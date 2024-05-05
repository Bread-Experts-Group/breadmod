package breadmod.recipe.serializers

import breadmod.recipe.AbstractFluidEnergyRecipe
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry

@Suppress("MemberVisibilityCanBePrivate")
class SimpleFluidEnergyRecipeSerializer<T: AbstractFluidEnergyRecipe>(
    val factory: (
        cId: ResourceLocation, cTime: Int, cEnergy: Int,
        cFluidsRequired: List<FluidStack>, cItemsRequired: List<ItemStack>,
        cFluidsOutput: List<FluidStack>, cItemsOutput: List<ItemStack>
    ) -> T
) : RecipeSerializer<T> {
    companion object {
        const val ENTRY_ID_KEY = "id"
        const val ENTRY_AMOUNT_KEY = "amount"
        const val ENERGY_KEY = "energy"
        const val TIME_KEY = "time"
        const val FLUIDS_KEY = "fluids"
        const val ITEMS_KEY = "items"
    }
    private fun <T> IForgeRegistry<T>.reifyEntryID(p0: JsonObject) = this.getValue(ResourceLocation(p0.get(ENTRY_ID_KEY).asString))!!

    private fun JsonArray.extractFluidList() = this.map { entryObject ->
        val entry = entryObject.asJsonObject
        FluidStack(ForgeRegistries.FLUIDS.reifyEntryID(entry), entry.get(ENTRY_AMOUNT_KEY).asInt)
    }
    private fun List<FluidStack>.jsonifyFluidList() = JsonArray().also {
        this.forEach { stack -> it.add(JsonObject().also { obj ->
            obj.addProperty(ENTRY_ID_KEY, ForgeRegistries.FLUIDS.getKey(stack.fluid).toString())
            obj.addProperty(ENTRY_AMOUNT_KEY, stack.amount)
        }) }
    }
    private fun JsonArray.extractItemList() = this.map { entryObject ->
        val entry = entryObject.asJsonObject
        ItemStack(ForgeRegistries.ITEMS.reifyEntryID(entry), entry.get(ENTRY_AMOUNT_KEY).asInt)
    }
    private fun List<ItemStack>.jsonifyItemList() = JsonArray().also {
        this.forEach { stack -> it.add(JsonObject().also { obj ->
            obj.addProperty(ENTRY_ID_KEY, ForgeRegistries.ITEMS.getKey(stack.item).toString())
            obj.addProperty(ENTRY_AMOUNT_KEY, stack.count)
        }) }
    }

    override fun fromJson(p0: ResourceLocation, p1: JsonObject): T {
        val required =  p1.getAsJsonObject("inputs")
        val outputs =  p1.getAsJsonObject("outputs")
        return factory(
            ResourceLocation(p1.get(ENTRY_ID_KEY).asString),
            p1.get(TIME_KEY).asInt,
            required.get(ENERGY_KEY).asInt,
            required.getAsJsonArray(FLUIDS_KEY).extractFluidList(),
            required.getAsJsonArray(ITEMS_KEY).extractItemList(),
            outputs.getAsJsonArray(FLUIDS_KEY).extractFluidList(),
            outputs.getAsJsonArray(ITEMS_KEY).extractItemList()
        )
    }

    fun toJson(to: JsonObject, location: ResourceLocation, time: Int, energy: Int, fluidList: List<FluidStack>, itemList: List<ItemStack>, fluidOutputs: List<FluidStack>, itemOutputs: List<ItemStack>) = to.also {
        it.addProperty(ENTRY_ID_KEY, location.toString())
        it.addProperty(TIME_KEY, time)
        it.add("inputs", JsonObject().also { required ->
            required.addProperty(ENERGY_KEY, energy)
            required.add(FLUIDS_KEY, fluidList.jsonifyFluidList())
            required.add(ITEMS_KEY, itemList.jsonifyItemList())
        })
        it.add("outputs", JsonObject().also { outputs ->
            outputs.add(FLUIDS_KEY, fluidOutputs.jsonifyFluidList())
            outputs.add(ITEMS_KEY, itemOutputs.jsonifyItemList())
        })
    }

    fun FriendlyByteBuf.readFluidList(): List<FluidStack> = List(this.readInt()) { this.readFluidStack() }
    fun FriendlyByteBuf.writeFluidList(fluidList: List<FluidStack>) { this.writeInt(fluidList.size); fluidList.forEach { this.writeFluidStack(it) } }
    fun FriendlyByteBuf.readItemList(): List<ItemStack> = List(this.readInt()) { this.readItem() }
    fun FriendlyByteBuf.writeItemList(itemList: List<ItemStack>) { this.writeInt(itemList.size); itemList.forEach { this.writeItem(it) } }

    override fun fromNetwork(p0: ResourceLocation, p1: FriendlyByteBuf): T =
        factory(ResourceLocation(p1.readUtf()), p1.readInt(), p1.readInt(), p1.readFluidList(), p1.readItemList(), p1.readFluidList(), p1.readItemList())

    override fun toNetwork(pBuffer: FriendlyByteBuf, pRecipe: T) {
        pBuffer.writeUtf(pRecipe.id.toString())
        pBuffer.writeInt(pRecipe.energy)
        pBuffer.writeInt(pRecipe.time)
        pBuffer.writeFluidList(pRecipe.fluidsRequired)
        pBuffer.writeItemList(pRecipe.itemsRequired)
        pBuffer.writeFluidList(pRecipe.fluidsOutput)
        pBuffer.writeItemList(pRecipe.itemsOutput)
    }
}