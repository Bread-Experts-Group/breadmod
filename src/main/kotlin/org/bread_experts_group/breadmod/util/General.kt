package org.bread_experts_group.breadmod.util

import com.google.gson.JsonObject
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.PlainTextContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.util.RaycastResult.RayCastResultType
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import java.util.function.Supplier

internal val formatArray: List<String> =
    listOf("y", "z", "a", "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y")

/**
 * Limits a number to 1000, and provides a keyword describing it in a shortened format.
 * For example, 1000 → "1", "k".
 *
 * @return A pair containing the limited number and the unit sign.
 * @param n The number to format.
 * @param unitOffset The offset to start at.
 * @param unitMax The maximum number to reach before moving to the next unit.
 * @return A pair containing the limited number and the unit.
 * @author Miko Elbrecht
 * @since 1.0
 * @see formatUnit
 * @see formatArray
 */
fun formatNumber(n: Double, unitOffset: Int = 0, unitMax: Int = 1000): Pair<Double, String> {
    var num = n
    var index = 8 + unitOffset
    while (num >= unitMax && index < formatArray.size - 1) {
        num /= unitMax
        index++
    }
    while (num < 1 && index > 0) {
        num *= unitMax
        index--
    }
    return num to formatArray[index]
}

/**
 * Formats a number.
 * @return The formatted number: `"X S / Y S W (Z%)"` assuming X is under Y, otherwise `"Y / X S W (Z%)"`.
 * @param from The number to format.
 * @param to The maximum number (Y).
 * @param unit The label to append at the end (W).
 * @param formatShort If the numbers should be shortened with a unit in [formatNumber] (S).
 * @param decimals The number of decimals to use when representing [from] / [to].
 * @param unitOffset The offset to start at in [formatNumber].
 * (Only applicable in [formatShort]).
 * @param unitMax The maximum number to reach before moving to the next unit in [formatNumber].
 * (Only applicable in [formatShort]).
 * @author Miko Elbrecht
 * @see formatNumber
 */
fun formatUnit(
    from: Double,
    to: Double,
    unit: String,
    formatShort: Boolean,
    decimals: Int,
    unitOffset: Int = 0,
    unitMax: Int = 1000
): String {
    val formatStr = "%.${decimals}f %s/ %.${decimals}f %s (%.${decimals}f%%)"
    val percent = (from / to) * 100
    if (formatShort) {
        val toFormat = formatNumber(to, unitOffset, unitMax)
        val fromFormat = formatNumber(from, unitOffset, unitMax)
        return String.format(
            formatStr,
            fromFormat.first, if (toFormat.second != fromFormat.second) "${fromFormat.second}$unit " else "",
            toFormat.first, toFormat.second + unit,
            percent
        )
    } else {
        return String.format(
            formatStr,
            from, "",
            to, unit,
            percent
        )
    }
}

/**
 * Writes a [Component] into a new [JsonObject].
 * @return The [JsonObject] containing the [Component].
 * @param component The [Component] to write into the [JsonObject].
 * @throws NotImplementedError If the [Component] contains contents not yet supported by this function.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun componentToJson(component: Component): JsonObject = JsonObject().also {
    when (val contents = component.contents) {
        is TranslatableContents -> {
            it.addProperty("type", "translate")
            it.addProperty("key", contents.key)
            it.addProperty("fallback", contents.fallback)
            if (contents.args.isNotEmpty())
                throw NotImplementedError("Arguments not supposed for jsonifying translatable contents - sorry!")
        }

        is PlainTextContents.LiteralContents -> {
            it.addProperty("type", "literal")
            it.addProperty("text", contents.text)
        }

        else -> throw NotImplementedError("Unknown contents: ${contents::class.qualifiedName}")
    }
}

/**
 * Checks if this [Fluid] can be represented under the given [TagKey].
 * @param tag The [TagKey] to check against.
 * @return `true` if this [Fluid] is represented by the [TagKey], `false` otherwise.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun isTag(tag: TagKey<Fluid>): Boolean = (BuiltInRegistries.FLUID.getTag(tag).get() == tag) /*?: false*/

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

inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: Supplier<A>): IntrinsicTagAppender<T> =
    this.also { this.add(*toAdd.map { it.get() }.toTypedArray()) }

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
 * A result of a raycast operation.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
sealed class RaycastResult(
    /**
     * The type of the result; either [RayCastResultType.ENTITY] or [RayCastResultType.BLOCK].
     * @author Miko Elbrecht
     * @since 1.0.0
     * @see RayCastResultType
     */
    val type: RayCastResultType,
    /**
     * The [Vec3] this raycast started at.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("unused")
    val startPosition: Vec3,
    /**
     * The [Vec3] this raycast ended at (either by missing or hitting something).
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    @Suppress("unused")
    val endPosition: Vec3,
    /**
     * The unit direction this raycast was aimed towards.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    val direction: Vec3
) {
    /**
     * The type of the result; either [RayCastResultType.ENTITY] or [RayCastResultType.BLOCK].
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    enum class RayCastResultType {
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
        @Suppress("unused")
        val blockState: BlockState,
        startPosition: Vec3, endPosition: Vec3, direction: Vec3
    ) : RaycastResult(RayCastResultType.BLOCK, startPosition, endPosition, direction)

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
    ) : RaycastResult(RayCastResultType.ENTITY, startPosition, endPosition, direction)

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