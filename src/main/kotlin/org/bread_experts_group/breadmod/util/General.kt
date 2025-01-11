package org.bread_experts_group.breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.util.RaycastResult.Companion.blockRaycast
import org.bread_experts_group.breadmod.util.RaycastResult.Companion.entityRaycast
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import java.util.function.Supplier
import kotlin.math.round

internal val formatArray: List<String> =
	listOf("q", "r", "y", "z", "a", "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q")

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
	var index = 10 + unitOffset
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
	}
	return String.format(
		formatStr,
		from, "",
		to, unit,
		percent
	)
}

/**
 * Checks if this [Fluid] can be represented under the given [TagKey].
 * @param tag The [TagKey] to check against.
 * @return `true` if this [Fluid] is represented by the [TagKey], `false` otherwise.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun isTag(tag: TagKey<Fluid>): Boolean = (BuiltInRegistries.FLUID.getTag(tag).get() == tag) /*?: false*/
inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: Supplier<A>): IntrinsicTagAppender<T> =
	this.also { this.add(*toAdd.map(Supplier<A>::get).toTypedArray()) }

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
		startPosition: Vec3, endPosition: Vec3, direction: Vec3
	) : RaycastResult(RayCastResultType.BLOCK, startPosition, endPosition, direction)

	/**
	 * A result of a raycast operation for an [Entity].
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	class Entity(
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

/**
 * Adds a [Vec3] to this [Vec3].
 * @return The sum of this [Vec3] and [other].
 * @param other The [Vec3] to add to this [Vec3].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
operator fun Vec3.plus(other: Vec3): Vec3 = Vec3(this.x + other.x, this.y + other.y, this.z + other.z)

/**
 * Scales this [Vec3] by the specified factor.
 * @return The scaled [Vec3].
 * @param scale The factor to scale this [Vec3] by.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
operator fun Vec3.times(scale: Double): Vec3 = this.scale(scale)
/// Face Targeting Functions ///
// https://github.com/GregTechCEu/GregTech/blob/master/src/main/java/gregtech/api/util/GTUtility.java#L325
/**
 * Targets the face the player is currently looking at.
 * Looking at edges targets the neighbouring face, corners target the opposite face.
 *
 * Function copied from GregTechCEu.
 */
fun targetFace(facing: Direction, x: Double, y: Double, z: Double): Direction {
	val opposite: Direction = facing.opposite
	when (facing) {
		DOWN, UP     -> {
			if (x < 0.25) {
				if (z < 0.25) return opposite
				if (z > 0.75) return opposite
				return Direction.WEST
			}
			if (x > 0.75) {
				if (z < 0.25) return opposite
				if (z > 0.75) return opposite
				return Direction.EAST
			}
			if (z < 0.25) return Direction.NORTH
			if (z > 0.75) return Direction.SOUTH
			return facing
		}
		NORTH, SOUTH -> {
			if (x < 0.25) {
				if (y < 0.25) return opposite
				if (y > 0.75) return opposite
				return Direction.WEST
			}
			if (x > 0.75) {
				if (y < 0.25) return opposite
				if (y > 0.75) return opposite
				return Direction.EAST
			}
			if (y < 0.25) return Direction.DOWN
			if (y > 0.75) return Direction.UP
			return facing
		}
		WEST, EAST   -> {
			if (z < 0.25) {
				if (y < 0.25) return opposite
				if (y > 0.75) return opposite
				return Direction.NORTH
			}
			if (z > 0.75) {
				if (y < 0.25) return opposite
				if (y > 0.75) return opposite
				return Direction.SOUTH
			}
			if (y < 0.25) return Direction.DOWN
			if (y > 0.75) return Direction.UP
			return facing
		}
	}
}

fun targetFaceSection(
	targetX: Double,
	targetY: Double,
	minX: Double,
	minY: Double,
	maxX: Double,
	maxY: Double
): Boolean = (targetX in minX .. maxX) && (targetY in minY .. maxY)

/**
 * Normalizes the absolute location of the hit result to 2 decimal places.
 */
fun normalizeHitLoc(hitLoc: Double, blockPos: Int): Double = round((hitLoc - blockPos) * 100) / 100
/// End Face Targeting Functions ///
/// !!! NOTICE !!! ///
// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good Javadoc for them!
/// INTERNAL DEFINITIONS FOLLOW ///