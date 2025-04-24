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
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagType
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.EntityGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.experimental.physics_grid.ClientPhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGridGlobals
import org.bread_experts_group.breadmod.experimental.physics_grid.ServerPhysicsGrid
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3i
import java.math.BigDecimal
import java.util.UUID
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
	Class.forName(path, true, T::class.java.classLoader).kotlin.createInstance() as T

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

/// Start raycast functions ///
class HitResult<T>(val position: Vec3, val length: Double, val side: Direction, val direction: Vec3, val hit: T) {
	val blockPosition: BlockPos
		get() = BlockPos.containing(this.position)
	val directionEnum: Direction
		get() = Direction.getNearest(this.direction)

	fun HitResult<Triple<PhysicsGrid, BlockPos, BlockState>>.getAsBlockHitResult(): GridBlockHitResult =
		GridBlockHitResult(
			this.position, this.directionEnum,
			this.blockPosition,
			this.hit.first,
			this.hit.second,
			this.hit.third
		)
}

fun <T> rayCast(
	position: Vec3, direction: Vec3,
	length: Double,
	selector: (Vec3) -> T?
): HitResult<T>? {
	var result: HitResult<T>? = null
	var distance = 0.0
	do {
		val localPosition = position.add(direction.scale(distance))
		val hit = selector(localPosition)
		if (hit != null) {
			result = HitResult(
				localPosition, length,
				Direction.getNearest(position).opposite, direction, hit
			)
			break
		}
		distance++
	} while (distance < length)
	return result
}

fun <T> Entity.rayCast(length: Double, selector: (Level, Vec3) -> T?): HitResult<T>? = rayCast(
	this.eyePosition,
	this.calculateViewVector(this.xRot, this.yRot),
	length
) { selector(this.level(), it) }

fun blockPhysicsGrid(filter: (PhysicsGrid) -> Boolean): (Vec3) -> Triple<PhysicsGrid, BlockPos, BlockState>? =
	{ position ->
		// TODO! This is very inefficient! Look into other methods common for raytracing like what NVIDIA PhysX does!
		var capturedState: Triple<PhysicsGrid, BlockPos, BlockState>? = null
		grid@ for ((_, grid) in PhysicsGridGlobals.grids) {
			if (!filter.invoke(grid)) continue@grid
			for ((offset, state) in grid.blocks)
				if ((position - (offset.center + grid.position)).length() < 1) {
					capturedState = Triple(grid, offset, state)
					break@grid
				}
		}
		capturedState
	}

fun blockPhysicsGridV(level: Level): (Vec3) -> Triple<PhysicsGrid, BlockPos, BlockState>? {
	val filter: (PhysicsGrid) -> Boolean =
		if (level.isClientSide) {
			{ it is ClientPhysicsGrid }
		} else {
			{ it is ServerPhysicsGrid }
		}
	return blockPhysicsGrid(filter)
}

fun blockPhysicsGridLV(level: Level): (Level, Vec3) -> Triple<PhysicsGrid, BlockPos, BlockState>? {
	val filter: (PhysicsGrid) -> Boolean =
		if (level.isClientSide) {
			{ it is ClientPhysicsGrid }
		} else {
			{ it is ServerPhysicsGrid }
		}
	val gridSearcher = blockPhysicsGrid(filter)
	return { _, v -> gridSearcher.invoke(v) }
}

fun blocks(vararg filterBlocks: Block): (BlockGetter, Vec3) -> BlockState? = { level, position ->
	val blockPos = BlockPos(position.toVec3i())
	val state = level.getBlockState(blockPos)
	if (filterBlocks.contains(state.block)) null
	else state
}

fun entities(vararg filterTypes: EntityType<*>): (EntityGetter, Vec3) -> Entity? = { level, position ->
	val entities = level.getEntities(null, AABB.ofSize(position, 1.0, 1.0, 1.0))
		.firstOrNull()
	if (entities == null || filterTypes.contains(entities.type)) null
	else entities
}
/// End raycast functions ///
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

fun Direction.toYRotFixed(): Float {
	val rotFix = if (this == SOUTH || this == NORTH) 180f else 0f
	return this.toYRot() + rotFix
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
 * Subtracts a [Vec3] from this [Vec3].
 * @return The difference of this [Vec3] and [other].
 * @param other The [Vec3] to subtract from this [Vec3].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
operator fun Vec3.minus(other: Vec3): Vec3 = Vec3(this.x - other.x, this.y - other.y, this.z - other.z)

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
inline fun <reified T> CompoundTag.getValue(value: String): T = when (T::class) {
	Tag::class         -> this.get(value) as T
	CompoundTag::class -> this.getCompound(value) as T
	Boolean::class     -> this.getBoolean(value) as T
	Int::class         -> this.getInt(value) as T
	Float::class       -> this.getFloat(value) as T
	Byte::class        -> this.getByte(value) as T
	ByteArray::class   -> this.getByteArray(value) as T
	Double::class      -> this.getDouble(value) as T
	IntArray::class    -> this.getIntArray(value) as T
	Long::class        -> this.getLong(value) as T
	LongArray::class   -> this.getLongArray(value) as T
	Short::class       -> this.getShort(value) as T
	String::class      -> this.getString(value) as T
	TagType::class     -> this.getTagType(value) as T
	UUID::class        -> this.getUUID(value) as T
	else               -> throw IllegalArgumentException("${T::class.simpleName} is not supported, sorry!")
}

inline fun <reified T> CompoundTag.putValue(key: String, value: T) {
	when (T::class) {
		Tag::class         -> this.put(key, value as Tag)
		CompoundTag::class -> this.put(key, value as CompoundTag)
		Boolean::class     -> this.putBoolean(key, value as Boolean)
		Int::class         -> this.putInt(key, value as Int)
		Float::class       -> this.putFloat(key, value as Float)
		Byte::class        -> this.putByte(key, value as Byte)
		ByteArray::class   -> this.putByteArray(key, value as ByteArray)
		Double::class      -> this.putDouble(key, value as Double)
		IntArray::class    -> this.putIntArray(key, value as IntArray)
		Long::class        -> this.putLong(key, value as Long)
		LongArray::class   -> this.putLongArray(key, value as LongArray)
		Short::class       -> this.putShort(key, value as Short)
		String::class      -> this.putString(key, value as String)
		UUID::class        -> this.putUUID(key, value as UUID)
		else               -> throw IllegalArgumentException("${T::class.simpleName} is not supported, sorry!")
	}
}
/// !!! NOTICE !!! ///
// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good Javadoc for them!
/// INTERNAL DEFINITIONS FOLLOW ///