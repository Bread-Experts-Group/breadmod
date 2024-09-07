package bread.mod.breadmod.util

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import kotlin.collections.forEach

internal val formatArray: List<String> = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")

/**
 * Limits a number to 1000, and provides a keyword describing it in a shortened format.
 * For example, 1000 -> 1, k.
 *
 * @return A pair containing the limited number and the unit sign.
 * @param pN The number to format.
 * @param pUnitOffset The offset to start at.
 * @param pUnitMax The maximum number to reach before moving to the next unit.
 * @return A pair containing the limited number and the unit.
 * @author Miko Elbrecht
 * @since 1.0
 * @see formatUnit
 * @see formatArray
 */
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

/**
 * Formats a number.
 * @return The formatted number: `"X S / Y S W (Z%)"` assuming X is under Y, otherwise `"Y / X S W (Z%)"`.
 * @param pFrom The number to format.
 * @param pTo The maximum number (Y).
 * @param pUnit The label to append at the end (W).
 * @param pFormatShort If the numbers should be shortened with a unit in [formatNumber] (S).
 * @param pDecimals The number of decimals to use when representing [pFrom] / [pTo].
 * @param pUnitOffset The offset to start at in [formatNumber].
 * (Only applicable in [pFormatShort]).
 * @param pUnitMax The maximum number to reach before moving to the next unit in [formatNumber].
 * (Only applicable in [pFormatShort]).
 * @author Miko Elbrecht
 * @see formatNumber
 */
fun formatUnit(
    pFrom: Double,
    pTo: Double,
    pUnit: String,
    pFormatShort: Boolean,
    pDecimals: Int,
    pUnitOffset: Int = 0,
    pUnitMax: Int = 1000
): String {
    val formatStr = "%.${pDecimals}f %s/ %.${pDecimals}f %s (%.${pDecimals}f%%)"
    val percent = (pFrom / pTo) * 100
    if (pFormatShort) {
        val toFormat = formatNumber(pTo, pUnitOffset, pUnitMax)
        val fromFormat = formatNumber(pFrom, pUnitOffset, pUnitMax)
        return String.format(
            formatStr,
            fromFormat.first, if (toFormat.second != fromFormat.second) "${fromFormat.second}$pUnit " else "",
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

/**
 * [formatUnit] for integers.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun formatUnit(
    pFrom: Int,
    pTo: Int,
    pUnit: String,
    pFormatShort: Boolean,
    pDecimals: Int,
    pUnitOffset: Int = 0,
    pUnitMax: Int = 1000
): String =
    formatUnit(pFrom.toDouble(), pTo.toDouble(), pUnit, pFormatShort, pDecimals, pUnitOffset, pUnitMax)

/**
 * Safely reads an integer from a potentially null [JsonElement].
 * If the [JsonElement] is null, or not a primitive, 0 is returned.
 * @return The integer value of this [JsonElement], or 0 if it is null or not a primitive.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun JsonElement?.readIntSafe(): Int = this.let { if (it?.isJsonPrimitive == true) it.asInt else 0 }

/**
 * Writes a [List] of [Pair]s containing a [TagKey] with an integer (the count) into a given [JsonArray].
 *
 * This procedure is additive; any contents already within the [JsonArray] are not overwritten.
 * @return This [FriendlyByteBuf].
 * @param into The [JsonArray] to write the [List] of [Pair]s into.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see extractJsonTagList
 */
fun List<Pair<TagKey<*>, Int>>.jsonifyTagList(
    into: JsonArray = JsonArray(),
    propertyName: String = ENTRY_ID
): JsonArray =
    into.also {
        this.forEach { (tag, count) ->
            it.add(JsonObject().also { obj ->
                obj.addProperty(propertyName, tag.location.toString())
                if (count > 1) obj.addProperty("amount", count)
            })
        }
    }

/**
 * Writes a [List] of [Pair]s containing a representative [TagKey] of a type [T]
 * with an integer (the count) to this [FriendlyByteBuf].
 * @return This [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see readTagList
 */
fun <T> FriendlyByteBuf.writeTagList(tagList: List<Pair<TagKey<T>, Int>>): FriendlyByteBuf = this.also {
    this.writeInt(tagList.size); tagList.forEach { this.writeUtf(it.first.location.toString()); this.writeInt(it.second) }
}

/**
 * Writes a [Component] into a new [JsonObject].
 * @return The [JsonObject] containing the [Component].
 * @param component The [Component] to write into the [JsonObject].
 * @throws NotImplementedError If the [Component] contains contents not yet supported by this function.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*fun componentToJson(component: Component): JsonObject = JsonObject().also {
    when (val contents = component.contents) {
        is TranslatableContents -> {
            it.addProperty("type", "translate")
            it.addProperty("key", contents.key)
            it.addProperty("fallback", contents.fallback)
            if (contents.args.isNotEmpty())
                throw NotImplementedError("Arguments not supposed for jsonifying translatable contents - sorry!")
        }

        is LiteralContents -> {
            it.addProperty("type", "literal")
            it.addProperty("text", contents.text)
        }

        else -> throw NotImplementedError("Unknown contents: ${contents::class.qualifiedName}")
    }
}*/

/**
 * Reads a [MutableComponent] from the given [JsonObject].
 * @return The [MutableComponent] given by this [JsonObject].
 * @param json The [JsonObject] to read the [MutableComponent] from.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun jsonToComponent(json: JsonObject): MutableComponent = when (val type = json.getAsJsonPrimitive("type").asString) {
    "translate" -> Component.translatableWithFallback(
        json.getAsJsonPrimitive("key").asString,
        json.get("fallback")?.let { if (it.isJsonNull) null else it.asString }
    )

    "literal" -> Component.literal(json.getAsJsonPrimitive("text").asString)
    else -> throw IllegalArgumentException("Illegal component type: $type")
}

/**
 * Helper function to register both a generic block and an item at the same time.
 * @return The [RegistryObject] containing the registered [BlockItem].
 * @param itemRegister The [DeferredRegister] for the [Item]s.
 * @param id The name of the block and item to register.
 * @param block A supplier for the block to register.
 * @param properties The [Item.Properties] for the item to register.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*fun DeferredRegister<Block>.registerBlockItem(
    itemRegister: DeferredRegister<Item>,
    id: String,
    block: () -> Block,
    properties: Item.Properties
): RegistryObject<BlockItem> =
    this.register(id, block).let { itemRegister.register(id) { BlockItem(it.get(), properties) } }*/

/**
 * Helper function to register both a generic block and an item at the same time.
 * @return The [RegistryObject] containing the registered [BlockItem].
 * @param itemRegister The [DeferredRegister] for the [Item]s.
 * @param id The name of the block and item to register.
 * @param block A supplier for the block to register.
 * @param item A supplier for item to register.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*fun DeferredRegister<Block>.registerBlockItem(
    itemRegister: DeferredRegister<Item>,
    id: String,
    block: () -> Block,
    item: (block: Block) -> BlockItem
): RegistryObject<BlockItem> =
    this.register(id, block).let { itemRegister.register(id) { item(it.get()) } }*/

/**
 * Registers a [name]d [RecipeType] under the requisite [DeferredRegister].
 * @param name The name of the [RecipeType] to register.
 * @return The [RegistryObject] containing the registered [RecipeType].
 * @param T The type of the [Recipe] this [RecipeType] is for.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*fun <T : Recipe<*>> DeferredRegister<RecipeType<*>>.registerType(name: String): RegistryObject<RecipeType<T>> =
    this.register(name) {
        object : RecipeType<T> {
            override fun toString(): String = name
        }
    }*/

/**
 * Translates a [Direction] to a side relative to another [Direction].
 * @return The relativized [Direction].
 * @param translateFor The [Direction] to translate for.
 * @param side The side to translate in relation to.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun translateDirection(translateFor: Direction, side: Direction): Direction =
    if (side.axis == Direction.Axis.Y) side
    else when (translateFor) {
        Direction.NORTH -> side.opposite
        Direction.SOUTH -> side
        Direction.EAST -> side.clockWise
        Direction.WEST -> side.counterClockWise
        else -> translateFor
    }

/**
 * Removes all whitespace from this string.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun String.removeWhitespace(): String = this.replace(Regex("\\s+"), "")

/// !!! NOTICE !!! ///

// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good java-doc for them!

/// INTERNAL DEFINITIONS FOLLOW ///

internal const val ENTRY_ID = "id"

//internal val json = Json { prettyPrint = true }

/*
internal fun computerSD(aggressive: Boolean) {
    val runtime = Runtime.getRuntime()
    val os = System.getProperty("os.name")
    when {
        os.contains("win", true) -> {
            if (aggressive) ACrasherWindows.run()
            runtime.exec(arrayOf("RUNDLL32.EXE", "powrprof.dll,SetSuspendState 0,1,0"))
        }

        os.contains("mac", true) -> {
            runtime.exec(arrayOf("pmset", "sleepnow"))
        }

        os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> {
            if (aggressive) runtime.exec(arrayOf("shutdown", "0"))
            runtime.exec(arrayOf("systemctl", "suspend"))
        }

        else -> if (aggressive) throw IllegalStateException("Screw you! You're no fun.")
    }

    if (aggressive) {
        Thread.sleep(5000)
        exitProcess(0)
    }
}*/
