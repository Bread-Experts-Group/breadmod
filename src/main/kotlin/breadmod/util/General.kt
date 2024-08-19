@file:Suppress("unused")

package breadmod.util

import breadmod.natives.windows.ACrasherWindows
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
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
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.IForgeRegistry
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.times
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.ln
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

// todo function does not account for vanilla items that don't require get() or key, causing an error that uses these methods
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

external fun computerSDwin()

fun computerSD(aggressive: Boolean) {
    val runtime = Runtime.getRuntime()
    val os = System.getProperty("os.name")
    when {
        os.contains("win", true) -> {
            if (aggressive) ACrasherWindows.run()
            //runtime.exec(arrayOf("RUNDLL32.EXE", "powrprof.dll,SetSuspendState 0,1,0"))
        }

        os.contains("mac", true) -> {
            runtime.exec(arrayOf("pmset", "sleepnow"))
        }

        os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> {
            if (aggressive) runtime.exec(arrayOf("shutdown", "0"))
            runtime.exec(arrayOf("systemctl", "suspend"))
        }

        else -> if(aggressive) throw IllegalStateException("Screw you! You're no fun.")
    }

    if(aggressive) {
        Thread.sleep(5000)
        exitProcess(0)
    }
}

fun DeferredRegister<Block>.registerBlockItem(itemRegister: DeferredRegister<Item>, id: String, block: () -> Block, properties: Item.Properties): RegistryObject<BlockItem> =
    this.register(id, block).let { itemRegister.register(id) { BlockItem(it.get(), properties) } }
fun DeferredRegister<Block>.registerBlockItem(itemRegister: DeferredRegister<Item>, id: String, block: () -> Block, item: (block: Block) -> BlockItem): RegistryObject<BlockItem> =
    this.register(id, block).let { itemRegister.register(id) { item(it.get()) } }

fun <T: Recipe<*>> DeferredRegister<RecipeType<*>>.registerType(name: String): RegistryObject<RecipeType<T>> =
    this.register(name) { object : RecipeType<T> { override fun toString(): String = name } }

fun translateDirection(translateFor: Direction, side: Direction): Direction =
    if(side.axis == Direction.Axis.Y) side
    else when(translateFor) {
        Direction.NORTH -> side.opposite
        Direction.SOUTH -> side
        Direction.EAST -> side.clockWise
        Direction.WEST -> side.counterClockWise
        else -> translateFor
    }