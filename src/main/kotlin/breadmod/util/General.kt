package breadmod.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import org.joml.Matrix4f
import org.joml.Vector2f
import java.awt.Color
import java.util.*
import kotlin.math.min

val formatArray = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")
fun formatNumber(pN: Double, pUnitOffset: Int): Pair<Double, String> {
    var num = pN
    var index = 3 + pUnitOffset
    while (num >= 1000 && index < formatArray.size - 1) {
        num /= 1000
        index++
    }
    while (num < 1 && index > 0) {
        num *= 1000
        index--
    }
    return num to formatArray[index]
}

fun formatUnit(pFrom: Double, pTo: Double, pUnit: String, pFormatShort: Boolean, pDecimals: Int, pUnitOffset: Int = 0): String {
    val formatStr = "%.${pDecimals}f %s/ %.${pDecimals}f %s (%.${pDecimals}f%%)"
    val percent = (pFrom / pTo) * 100
    if (pFormatShort) {
        val toFormat = formatNumber(pTo, pUnitOffset)
        val fromFormat = formatNumber(pFrom, pUnitOffset)
        return String.format(
            formatStr,
            fromFormat.first, if(toFormat.second != fromFormat.second) "${fromFormat.second}$pUnit " else "",
            toFormat.first, toFormat.second + pUnit,
            percent
        )
    } else {
        return String.format(
            formatStr,
            pFrom, "",
            pTo, pUnit,
            percent
        )
    }
}
fun formatUnit(from: Int, to: Int, unit: String, formatShort: Boolean, places: Int, offset: Int = 0): String =
    formatUnit(from.toDouble(), to.toDouble(), unit, formatShort, places, offset)

fun GuiGraphics.renderFluid(
    pX: Float, pY: Float, pWidth: Int, pHeight: Int,
    pFluid: Fluid, pFlowing: Boolean, pDirection: Direction = Direction.NORTH
) {
    val atlas = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
    val ext = IClientFluidTypeExtensions.of(pFluid)
    val spriteDiff = if(pFlowing) {
        val stillWidth = atlas.apply(ext.stillTexture).contents().width().toFloat()
        atlas.apply(ext.flowingTexture).let { val flowingWidth = it.contents().width(); it to if(flowingWidth > stillWidth) (stillWidth / flowingWidth) else 1F }
    } else atlas.apply(ext.stillTexture) to 1F

    val sprite = spriteDiff.first
    val colors = FloatArray(4).also { Color(ext.tintColor).getComponents(it) }
    val matrix4f: Matrix4f = this.pose().last().pose()
    RenderSystem.setShaderTexture(0, sprite.atlasLocation())
    RenderSystem.setShader { GameRenderer.getPositionColorTexShader() }
    RenderSystem.enableBlend()
    val bufferBuilder = Tesselator.getInstance().builder
    bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX)

    val pX2 = pX + pWidth

    var remainingFluid = pHeight
    while(remainingFluid > 0) {
        // TODO: Make pY the TOP LEFT, instead of BOTTOM LEFT
        val lpY = (pY - remainingFluid); val lpY2 = lpY + min(remainingFluid, pWidth)

        // N  // E  // S  // W
        // AB // CA // DC // BD
        // CD // DB // BA // AC
        // (pX, lpY2), (pX2, lpY2)
        // (pX, lpY ), (pX2, lpY )
        val rotated = listOf(Vector2f(pX, lpY), Vector2f(pX, lpY2), Vector2f(pX2, lpY2), Vector2f(pX2, lpY)).also {
            Collections.rotate(
                it,
                when(pDirection) { Direction.EAST -> 1; Direction.SOUTH -> 2; Direction.WEST -> 3; else -> 0 }
            )
        }

        val dv1 = (sprite.v1 - sprite.v0)
        val v1 = if(remainingFluid < pWidth) (sprite.v0 + ((dv1 / pWidth) * remainingFluid)) else (sprite.v0 + (dv1 * spriteDiff.second))
        val u1 = sprite.u0 + ((sprite.u1 - sprite.u0) * spriteDiff.second)

        fun VertexConsumer.color() = this.color(colors[0], colors[1], colors[2], colors[3])
        rotated[0].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(       u1,        v1).endVertex() }
        rotated[1].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(       u1, sprite.v0).endVertex() }
        rotated[2].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(sprite.u0, sprite.v0).endVertex() }
        rotated[3].let { bufferBuilder.vertex(matrix4f, it.x, it.y, 0F).color().uv(sprite.u0,        v1).endVertex() }

        remainingFluid -= pWidth
    }

    BufferUploader.drawWithShader(bufferBuilder.end())
    RenderSystem.disableBlend()
}

const val ENTRY_ID_KEY = "id"
const val ENTRY_AMOUNT_KEY = "amount"
fun <T> IForgeRegistry<T>.reifyEntryID(p0: JsonObject) = this.getValue(
    ResourceLocation(p0.get(
        ENTRY_ID_KEY
    ).asString)
)!!

fun JsonElement?.readIntSafe(): Int = this.let { if(it?.isJsonPrimitive == true) it.asInt else 1 }
fun JsonArray.extractJsonFluidList() = this.map { entryObject ->
    val entry = entryObject.asJsonObject
    FluidStack(ForgeRegistries.FLUIDS.reifyEntryID(entry), entry.get(ENTRY_AMOUNT_KEY).readIntSafe())
}
fun List<FluidStack>.jsonifyFluidList(into: JsonArray = JsonArray(), keyUseName: String = ENTRY_ID_KEY) = into.also {
    this.forEach { stack -> it.add(JsonObject().also { obj ->
        obj.addProperty(keyUseName, ForgeRegistries.FLUIDS.getKey(stack.fluid).toString())
        if(stack.amount > 1) obj.addProperty(ENTRY_AMOUNT_KEY, stack.amount)
    }) }
}
fun JsonArray.extractJsonItemList() = this.map { entryObject ->
    val entry = entryObject.asJsonObject
    ItemStack(ForgeRegistries.ITEMS.reifyEntryID(entry), entry.get(ENTRY_AMOUNT_KEY).readIntSafe())
}
fun List<ItemStack>.jsonifyItemList(into: JsonArray = JsonArray(), keyUseName: String = ENTRY_ID_KEY) = into.also {
    this.forEach { stack -> it.add(JsonObject().also { obj ->
        obj.addProperty(keyUseName, ForgeRegistries.ITEMS.getKey(stack.item).toString())
        if(stack.count > 1) obj.addProperty(ENTRY_AMOUNT_KEY, stack.count)
    }) }
}

fun <T> IForgeRegistry<T>.createTagKey(path: String): TagKey<T> = TagKey.create(this.registryKey, ResourceLocation(path))
fun <T> JsonArray.extractJsonTagList(registry: IForgeRegistry<T>, keyUseName: String = ENTRY_ID_KEY) = this.map { entryObject ->
    val entry = entryObject.asJsonObject
    registry.createTagKey(entry.get(keyUseName).asString) to entry.get(ENTRY_AMOUNT_KEY).readIntSafe()
}
fun List<Pair<TagKey<*>, Int>>.jsonifyTagList(into: JsonArray = JsonArray(), keyUseName: String = ENTRY_ID_KEY) = into.also {
    this.forEach { pair -> it.add(JsonObject().also { obj ->
        obj.addProperty(keyUseName, pair.first.location.toString())
        if(pair.second > 1) obj.addProperty(ENTRY_AMOUNT_KEY, pair.second)
    }) }
}

fun FriendlyByteBuf.readFluidList(): List<FluidStack> = List(this.readInt()) { this.readFluidStack() }
fun FriendlyByteBuf.writeFluidList(fluidList: List<FluidStack>) { this.writeInt(fluidList.size); fluidList.forEach { this.writeFluidStack(it) } }
fun FriendlyByteBuf.readItemList(): List<ItemStack> = List(this.readInt()) { this.readItem() }
fun FriendlyByteBuf.writeItemList(itemList: List<ItemStack>) { this.writeInt(itemList.size); itemList.forEach { this.writeItem(it) } }
fun <T> FriendlyByteBuf.readTagList(registry: IForgeRegistry<T>): List<Pair<TagKey<T>, Int>> = List(this.readInt()) { registry.createTagKey(this.readUtf()) to this.readInt() }
fun <T> FriendlyByteBuf.writeTagList(tagList: List<Pair<TagKey<T>, Int>>) { this.writeInt(tagList.size); tagList.forEach { this.writeUtf(it.first.location.toString()); this.writeInt(it.second)} }

fun Collection<ItemStack>.serialize(tag: CompoundTag): CompoundTag {
    this.forEachIndexed { index, stack -> tag.put(index.toString(), stack.serializeNBT()) }
    return tag
}
fun Collection<ItemStack>.serialize() = this.serialize(CompoundTag())
fun MutableList<ItemStack>.deserialize(tag: CompoundTag) = tag.allKeys.forEach { this[it.toInt()] = ItemStack.of(tag.getCompound(it)) }

fun IFluidHandler.amount(fluid: Fluid) =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isSame(fluid) }
        .sumOf { it.amount }
fun IFluidHandler.amount(fluid: TagKey<Fluid>) =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.`is`(fluid) }
        .sumOf { it.amount }