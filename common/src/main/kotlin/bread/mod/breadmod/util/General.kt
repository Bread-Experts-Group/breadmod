package bread.mod.breadmod.util

import bread.mod.breadmod.registry.config.ConfigValue
import bread.mod.breadmod.util.RaycastResult.RaycastResultType
import dev.architectury.registry.registries.DeferredRegister
import dev.architectury.registry.registries.RegistrySupplier
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Vec3i
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.reflect.KProperty1

internal val formatArray: List<String> = listOf("p", "n", "m", "", "k", "M", "G", "T", "P", "E")

/**
 * Used for setting common values on clientside when joining/leaving a server
 * - Left side is the old value on client, right side is the new value applied.
 */
internal val configValuesMap: MutableMap<ConfigValue<*>, ConfigValue<*>> = mutableMapOf()

/**
 * Limits a number to 1000, and provides a keyword describing it in a shortened format.
 * For example, 1000 → "1", "k".
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

// --Commented out by Inspection START (9/10/2024 03:52):
///**
// * [formatUnit] for integers.
// * @author Miko Elbrecht
// * @since 1.0.0
// */
//fun formatUnit(
//    pFrom: Int,
//    pTo: Int,
//    pUnit: String,
//    pFormatShort: Boolean,
//    pDecimals: Int,
//    pUnitOffset: Int = 0,
//    pUnitMax: Int = 1000
//): String =
//    formatUnit(pFrom.toDouble(), pTo.toDouble(), pUnit, pFormatShort, pDecimals, pUnitOffset, pUnitMax)
// --Commented out by Inspection STOP (9/10/2024 03:52)

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

/**
 * Registers a [name]d [RecipeType] under the requisite [DeferredRegister].
 * @param name The name of the [RecipeType] to register.
 * @return The [RegistrySupplier] containing the registered [RecipeType].
 * @param T The type of the [Recipe] this [RecipeType] is for.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun <T : Recipe<*>> DeferredRegister<RecipeType<*>>.registerType(name: ResourceLocation): RegistrySupplier<RecipeType<T>> {
    val string = name.toString()
    return this.register(name) {
        object : RecipeType<T> {
            override fun toString(): String = string
        }
    }
}

/**
 * A result of a raycast operation.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
sealed class RaycastResult(
    /**
     * The type of the result; either [RaycastResultType.ENTITY] or [RaycastResultType.BLOCK].
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see RaycastResultType
     */
    val type: RaycastResultType,
    /**
     * The [Vec3] this raycast started at.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val startPosition: Vec3,
    /**
     * The [Vec3] this raycast ended at (either by missing or hitting something).
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val endPosition: Vec3,
    /**
     * The unit direction this raycast was aimed towards.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val direction: Vec3
) {
    /**
     * The type of the result; either [RaycastResultType.ENTITY] or [RaycastResultType.BLOCK].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    enum class RaycastResultType {
        /**
         * The result was for detecting an [Entity].
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        ENTITY,

        /**
         * The result was for detecting [Block]s.
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        BLOCK
    }

    /**
     * A result of a raycast operation for [Block]s.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    class Block(
        /**
         * The [BlockState] this raycast operation hit in a [Level].
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        val blockState: BlockState,
        startPosition: Vec3, endPosition: Vec3, direction: Vec3
    ) : RaycastResult(RaycastResultType.BLOCK, startPosition, endPosition, direction)

    /**
     * A result of a raycast operation for an [Entity].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    class Entity(
        /**
         * The [Entity] this raycast operation hit in a [Level].
         * @author Miko Elbrecht
         * @since 1.0.0
         */
        val entity: net.minecraft.world.entity.Entity,
        startPosition: Vec3, endPosition: Vec3, direction: Vec3
    ) : RaycastResult(RaycastResultType.ENTITY, startPosition, endPosition, direction)

    companion object {
        /**
         * Raycasts from [origin] in [direction] for [length] in a [Level],
         * returning the first [Entity] hit (if any).
         * @return The [Entity] hit by this raycast, or `null` if no [Entity] was hit.
         * @param exclude The [Entity] to exclude from the raycast.
         * @param origin The [Vec3] to start the raycast from.
         * @param direction The unit direction to raycast in.
         * @param length The maximum length of the raycast.
         * @author Miko Elbrecht
         * @since 1.0.0
         * @see blockRaycast
         * @see Entity
         */
        fun Level.entityRaycast(
            exclude: net.minecraft.world.entity.Entity?,
            origin: Vec3,
            direction: Vec3,
            length: Double
        ): Entity? {
            var distance = 0.0
            while (true) {
                val position = origin + (direction * distance)
                val entities = this.getEntities(exclude, AABB.ofSize(position, 10.0, 10.0, 10.0))
                if (entities.isNotEmpty()) entities.forEach {
                    if (it.getDimensions(it.pose).makeBoundingBox(it.position()).contains(position)) return Entity(
                        it,
                        origin,
                        position,
                        direction
                    )
                }
                if (distance > length) return null
                distance += 0.1
            }
        }

        /**
         * Raycasts from [origin] in [direction] for [length] in a [Level],
         * returning the first [Block] hit (if any).
         * @return The [Block] hit by this raycast, or `null` if no [Block] was hit.
         * @param origin The [Vec3] to start the raycast from.
         * @param direction The unit direction to raycast in.
         * @param length The maximum length of the raycast.
         * @param countFluid If fluids should be counted as hits.
         * @author Miko Elbrecht
         * @since 1.0.0
         * @see entityRaycast
         * @see Block
         */
        fun Level.blockRaycast(
            origin: Vec3,
            direction: Vec3,
            length: Double,
            countFluid: Boolean
        ): Block? {
            var distance = 0.0
            while (true) {
                val position = origin + (direction * distance)
                val state = this.getBlockState(BlockPos(position.toVec3i()))
                if (!state.isAir && (countFluid || state.fluidState.type != Fluids.EMPTY)) return Block(
                    state,
                    origin,
                    position,
                    direction
                )
                if (distance > length) return null
                distance += 0.1
            }
        }
    }
}

fun Vec3.toVec3i(): Vec3i = Vec3i(Mth.floor(x), Mth.floor(y), Mth.floor(z))

// --Commented out by Inspection START (9/10/2024 03:52):
///**
// * Writes a [Component] into a new [JsonObject].
// * @return The [JsonObject] containing the [Component].
// * @param component The [Component] to write into the [JsonObject].
// * @throws NotImplementedError If the [Component] contains contents not yet supported by this function.
// * @author Miko Elbrecht
// * @since 1.0.0
// */
//fun componentToJson(component: Component): JsonObject = JsonObject().also {
//    when (val contents = component.contents) {
//        is TranslatableContents -> {
//            it.addProperty("type", "translate")
//            it.addProperty("key", contents.key)
//            it.addProperty("fallback", contents.fallback)
//            if (contents.args.isNotEmpty())
//                throw NotImplementedError("Arguments not supposed for jsonifying translatable contents - sorry!")
//        }
//
//        is LiteralContents -> {
//            it.addProperty("type", "literal")
//            it.addProperty("text", contents.text)
//        }
//
//        else -> throw NotImplementedError("Unknown contents: ${contents::class.qualifiedName}")
//    }
//}
// --Commented out by Inspection STOP (9/10/2024 03:52)

// --Commented out by Inspection START (9/10/2024 03:52):
///**
// * Reads a [MutableComponent] from the given [JsonObject].
// * @return The [MutableComponent] given by this [JsonObject].
// * @param json The [JsonObject] to read the [MutableComponent] from.
// * @author Miko Elbrecht
// * @since 1.0.0
// */
//fun jsonToComponent(json: JsonObject): MutableComponent = when (val type = json.getAsJsonPrimitive("type").asString) {
//    "translate" -> Component.translatableWithFallback(
//        json.getAsJsonPrimitive("key").asString,
//        json.get("fallback")?.let { if (it.isJsonNull) null else it.asString }
//    )
//
//    "literal" -> Component.literal(json.getAsJsonPrimitive("text").asString)
//    else -> throw IllegalArgumentException("Illegal component type: $type")
//}
// --Commented out by Inspection STOP (9/10/2024 03:52)

/**
 * Adds a [Vec3] to this [Vec3].
 * @return The sum of this [Vec3] and [other].
 * @param other The [Vec3] to add to this [Vec3].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
operator fun Vec3.plus(other: Vec3): Vec3 = Vec3(x + other.x, y + other.y, z + other.z)

/**
 * Scales this [Vec3] by the specified factor.
 * @return The scaled [Vec3].
 * @param scale The factor to scale this [Vec3] by.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
operator fun Vec3.times(scale: Double): Vec3 = this.scale(scale)

/// !!! NOTICE !!! ///

// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good Javadoc for them!

/// INTERNAL DEFINITIONS FOLLOW ///

// --Commented out by Inspection START (9/10/2024 03:51):
//internal fun computerSD(aggressive: Boolean) {
//    val runtime = Runtime.getRuntime()
//    val os = System.getProperty("os.name")
//    when {
//        os.contains("win", true) -> {
////            if (aggressive) ACrasherWindows.run()
//            runtime.exec(arrayOf("RUNDLL32.EXE", "powrprof.dll,SetSuspendState 0,1,0"))
//        }
//
//        os.contains("mac", true) -> {
//            runtime.exec(arrayOf("pmset", "sleepnow"))
//        }
//
//        os.contains("nix", true) || os.contains("nux", true) || os.contains("aix", true) -> {
//            if (aggressive) runtime.exec(arrayOf("shutdown", "0"))
//            runtime.exec(arrayOf("systemctl", "suspend"))
//        }
//
//        else -> if (aggressive) throw IllegalStateException("Screw you! You're no fun.")
//    }
//
//    if (aggressive) {
//        Thread.sleep(5000)
//        exitProcess(0)
//    }
// --Commented out by Inspection STOP (9/10/2024 03:51)
//}

internal inline fun <reified T> Any?.ensureRegistrySupplierAndValue(forProperty: KProperty1<*, *>): RegistrySupplier<T> {
    val value = this.ensureRegistrySupplier(forProperty).get()
    if (value !is T)
        throw IllegalArgumentException("${forProperty.name} must supply type ${T::class.qualifiedName}")

    @Suppress("UNCHECKED_CAST")
    return this as RegistrySupplier<T>
}

internal fun Any?.ensureRegistrySupplier(forProperty: KProperty1<*, *>): RegistrySupplier<*> {
    if (this !is RegistrySupplier<*>)
        throw IllegalArgumentException("${forProperty.name} must be of type ${RegistrySupplier::class.qualifiedName}.")
    return this
}