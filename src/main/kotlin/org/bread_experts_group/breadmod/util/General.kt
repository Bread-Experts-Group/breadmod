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
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.util.RaycastResult.Companion.blockRaycast
import org.bread_experts_group.breadmod.util.RaycastResult.Companion.entityRaycast
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus
import java.math.BigDecimal
import java.util.function.Supplier
import kotlin.math.round
import kotlin.reflect.full.createInstance

internal val formatArray: List<String> =
	listOf("q", "r", "y", "z", "a", "f", "p", "n", "µ", "m", "", "k", "M", "G", "T", "P", "E", "Z", "Y", "R", "Q")

fun BigDecimal.capInt(): Int = if (this > Int.MAX_VALUE.toBigDecimal()) Int.MAX_VALUE else this.toInt()

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
 * @see formatArray
 */
fun formatNumberBigDecimal(
	n: BigDecimal,
	unitOffset: Int = 0,
	unitMax: BigDecimal = BigDecimal.valueOf(1000)
): Pair<BigDecimal, String> {
	var num = n
	var index = 10 + unitOffset
	while (num >= unitMax && index < formatArray.size - 1) {
		num = num.divide(unitMax)
		index++
	}
	while (num < BigDecimal.ONE && index > 0) {
		num = num.multiply(unitMax)
		index--
	}
	return num to formatArray[index]
}

/**
 * Retrieves an instance of the provided [path]
 * @throws [NoClassDefFoundError] if [path] is invalid or class does not exist.
 */
inline fun <reified T> toClass(path: String): T =
	Class.forName(
		path,
		true,
		T::class.java.classLoader
	).kotlin.createInstance() as T

/**
 * Converts a given [Class] to it's qualified name.
 */
inline fun <reified T> fromClass(clazz: T): String = (clazz ?: "")::class.qualifiedName!!

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
		NORTH -> side.opposite
		SOUTH -> side
		EAST  -> side.clockWise
		WEST  -> side.counterClockWise
		else  -> translateFor
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

fun BlockPos.offset(vec3: Vec3): Vec3 =
	if (vec3.x == 0.0 && vec3.y == 0.0 && vec3.z == 0.0) this.toVec3() else
		Vec3(this.x.toDouble() + vec3.x, this.y.toDouble() + vec3.y, this.z.toDouble() + vec3.z)

fun BlockPos.subtract(vec3: Vec3): Vec3 = this.offset(-vec3)

/// AABB Operations ///
fun centerAABB(pos: BlockPos): AABB = AABB(pos.center, pos.center)
fun AABB.threeByThree(): AABB = this.inflate(1.0, 1.0, 1.0)
fun threeByThreeAABB(pos: BlockPos): AABB = centerAABB(pos).threeByThree()
/// End AABB Operations ///
/**
 * Converts this [BlockPos] to an [IntArray].
 */
fun BlockPos.toIntArray(): IntArray = intArrayOf(this.x, this.y, this.z)

/**
 * Converts this [IntArray] to a [BlockPos].
 */
fun IntArray.toBlockPos(): BlockPos {
	if (this.size != 3) return BlockPos.ZERO
	return BlockPos(this[0], this[1], this[2])
}

fun getStackInPlayerHand(player: Player?, hand: InteractionHand = player?.usedItemHand ?: MAIN_HAND): ItemStack {
	if (player == null) return ItemStack.EMPTY
	return player.getItemInHand(hand)
}

/// Face Targeting Functions ///
fun dunsxCheck(opposite: Direction, x: Double, z: Double): Direction? {
	if (x < 0.25) {
		if (z < 0.25) return opposite
		if (z > 0.75) return opposite
		return WEST
	}
	if (x > 0.75) {
		if (z < 0.25) return opposite
		if (z > 0.75) return opposite
		return EAST
	}
	return null
}
// https://github.com/GregTechCEu/GregTech/blob/master/src/main/java/gregtech/api/util/GTUtility.java#L325
/**
 * Targets the face the player is currently looking at.
 * Looking at edges, targets the neighboring face; corners target the opposite face.
 *
 * Function copied from GregTechCEu.
 */
fun targetFace(facing: Direction, x: Double, y: Double, z: Double): Direction {
	val opposite: Direction = facing.opposite
	when (facing) {
		DOWN, UP     -> {
			return dunsxCheck(opposite, x, z) ?: facing.let {
				if (z < 0.25) return NORTH
				if (z > 0.75) return SOUTH
				it
			}
		}
		NORTH, SOUTH -> {
			return dunsxCheck(opposite, x, z) ?: facing.let {
				if (y < 0.25) return DOWN
				if (y > 0.75) return UP
				it
			}
		}
		WEST, EAST   -> {
			if (z < 0.25) {
				if (y < 0.25) return opposite
				if (y > 0.75) return opposite
				return NORTH
			}
			if (z > 0.75) {
				if (y < 0.25) return opposite
				if (y > 0.75) return opposite
				return SOUTH
			}
			if (y < 0.25) return DOWN
			if (y > 0.75) return UP
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
 * Normalizes the absolute location of the hit result to two decimal places.
 */
fun normalizeHitLoc(hitLoc: Double, blockPos: Int): Double = round((hitLoc - blockPos) * 100) / 100
fun normalizedHitPos(hitLoc: Vec3, blockPos: BlockPos): Vec3 {
	val x = normalizeHitLoc(hitLoc.x, blockPos.x)
	val y = normalizeHitLoc(hitLoc.y, blockPos.y)
	val z = normalizeHitLoc(hitLoc.z, blockPos.z)
	return Vec3(x, y, z)
}

fun horizontalDirectionalTargetFaceSection(
	direction: Direction,
	targetPos: Vec3,
	minxXNorthEast: Double,
	maxXNorthEast: Double,
	minxXSouthWest: Double,
	maxXSouthWest: Double,
	minY: Double,
	maxY: Double
): Boolean = when (direction) {
	NORTH -> targetFaceSection(targetPos.x, targetPos.y, minxXNorthEast, minY, maxXNorthEast, maxY)
	SOUTH -> targetFaceSection(targetPos.x, targetPos.y, minxXSouthWest, minY, maxXSouthWest, maxY)
	WEST  -> targetFaceSection(targetPos.z, targetPos.y, minxXSouthWest, minY, maxXSouthWest, maxY)
	EAST  -> targetFaceSection(targetPos.z, targetPos.y, minxXNorthEast, minY, maxXNorthEast, maxY)
	else  -> false
}
/// End Face Targeting Functions ///
/// !!! NOTICE !!! ///
// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good Javadoc for them!
/// INTERNAL DEFINITIONS FOLLOW ///