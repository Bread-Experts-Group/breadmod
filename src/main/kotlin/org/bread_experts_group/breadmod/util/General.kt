package org.bread_experts_group.breadmod.util

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.core.Holder
import net.minecraft.core.NonNullList
import net.minecraft.core.Vec3i
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TagType
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.tags.TagKey
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffectUtil
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.animal.Pig
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeInput
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.EntityGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.Rotation.CLOCKWISE_180
import net.minecraft.world.level.block.Rotation.CLOCKWISE_90
import net.minecraft.world.level.block.Rotation.COUNTERCLOCKWISE_90
import net.minecraft.world.level.block.Rotation.NONE
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.Shapes.or
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.capabilities.BlockCapability
import org.joml.Vector3f
import java.lang.reflect.Method
import java.math.BigDecimal
import java.util.UUID
import java.util.function.Supplier
import java.util.stream.Stream
import kotlin.jvm.optionals.getOrNull
import kotlin.math.round
import kotlin.reflect.full.createInstance

val Vector3fZero: Vector3f = Vector3f(0f, 0f, 0f)
val Vector3fAxisX: Vector3f = Vector3f(1f, 0f, 0f)
val Vector3fAxisZ: Vector3f = Vector3f(0f, 0f, 1f)

val HORIZONTAL_DIRECTIONS: Array<Direction> = Direction.entries.filter { it.axis.isHorizontal }.toTypedArray()
val ALL_DIRECTIONS: Array<Direction> = Direction.entries.toTypedArray()

fun BigDecimal.capInt(): Int = if (this > Int.MAX_VALUE.toBigDecimal()) Int.MAX_VALUE else this.toInt()

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
fun isTag(tag: TagKey<Fluid>): Boolean = BuiltInRegistries.FLUID.get(tag.location) == tag
inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: Supplier<A>): IntrinsicTagAppender<T> =
	this.also { this.add(*toAdd.map(Supplier<A>::get).toTypedArray()) }

private val getCapMethod: Method = Level::class.java.getMethod(
	"getCapability",
	BlockCapability::class.java,
	BlockPos::class.java,
	BlockState::class.java,
	BlockEntity::class.java
)

@Suppress("UNCHECKED_CAST")
fun <T> Level.getCapability(
	capability: BlockCapability<T, *>,
	pos: BlockPos,
	state: BlockState,
	entity: BlockEntity
): T? = getCapMethod.invoke(this, capability, pos, state, entity) as T?

private val shapeOrigin: Vec3 = Vec3(-0.5, -0.5, -0.5)

fun AABB.rotate(rotation: Rotation): AABB = when (rotation) {
	NONE                -> this
	CLOCKWISE_90        -> AABB(-this.minZ, this.minY, this.minX, -this.maxZ, this.maxY, this.maxX)
	CLOCKWISE_180       -> AABB(-this.minX, this.minY, -this.minZ, -this.maxX, this.maxY, -this.maxZ)
	COUNTERCLOCKWISE_90 -> AABB(this.minZ, this.minY, -this.minX, this.maxZ, this.maxY, -this.maxX)
}

fun Stream<VoxelShape>.combine(): VoxelShape = this.reduce(::or).get()
fun combineShapes(list: List<VoxelShape>): VoxelShape = list.stream().reduce(::or).get()

fun VoxelShape.rotate(rotation: Rotation): VoxelShape = combineShapes(
	this.toAabbs().map { Shapes.create(it.move(shapeOrigin).rotate(rotation).move(-shapeOrigin)) }
)

/// Start raycast functions ///
class HitResult<T>(
	val position: Vec3,
	val blockPosition: BlockPos,
	val length: Double,
	val side: Direction,
	val direction: Vec3,
	val hit: T
)

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
			val blockPos = BlockPos.containing(localPosition)
			result = HitResult(
				localPosition, blockPos, length,
				Direction.getNearest(normalizedHitPos(localPosition, blockPos)), direction, hit
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

//data class GridHitResult(
//	val grid: PhysicsGrid,
//	val state: BlockState,
//	val hitResult: net.minecraft.world.phys.HitResult
//)
//
//fun blockPhysicsGrid(
//	filter: (PhysicsGrid) -> Boolean,
//	from: Vec3, to: Vec3, hitFluids: Boolean,
//	collisionContext: CollisionContext
//): GridHitResult? {
//	return null
//	grid@ for ((_, grid) in PhysicsGridGlobals.grids) {
//		if (!filter.invoke(grid)) continue@grid
//		val hitResult = grid.clip(
//			ClipContext(
//				from, to, ClipContext.Block.OUTLINE,
//				if (hitFluids) ClipContext.Fluid.ANY else ClipContext.Fluid.NONE,
//				collisionContext
//			)
//		)
//		if (hitResult.type != net.minecraft.world.phys.HitResult.Type.MISS)
//			return GridHitResult(grid, grid.getBlockState(hitResult.blockPos), hitResult)
//	}
//	return null
//}
fun Vec3.toVec3i(): Vec3i = Vec3i(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z))
fun Vector3f.toVec3(): Vec3 = Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
fun Vec3i.toVec3(): Vec3 = Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
operator fun Vec3.unaryMinus(): Vec3 = Vec3(-this.x, -this.y, -this.z)
operator fun Vec3i.unaryMinus(): Vec3i = Vec3i(-this.x, -this.y, -this.z)

operator fun Vec3.component1(): Double = this.x
operator fun Vec3.component2(): Double = this.y
operator fun Vec3.component3(): Double = this.z
operator fun BlockPos.component1(): Int = this.x
operator fun BlockPos.component2(): Int = this.y
operator fun BlockPos.component3(): Int = this.z
operator fun MobEffectInstance.component1(): Holder<MobEffect> = this.effect
operator fun MobEffectInstance.component2(): Int = this.amplifier

fun blocks(vararg filterBlocks: Block = arrayOf(Blocks.AIR)): (BlockGetter, Vec3) -> BlockState? = { level, position ->
	val blockPos = BlockPos(position.toVec3i())
	val state = level.getBlockState(blockPos)
	if (filterBlocks.contains(state.block)) null
	else state
}

fun entities(vararg filterTypes: EntityType<*> = arrayOf(EntityType.PLAYER)): (EntityGetter, Vec3) -> Entity? =
	{ level, position ->
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

fun Vec3.plus(x: Double, y: Double, z: Double): Vec3 = Vec3(this.x + x, this.y + y, this.z + z)

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

val romanNumerals: List<Pair<String, Int>> = listOf(
	"M" to 1000,
	"D" to 500,
	"C" to 100,
	"L" to 50,
	"XL" to 40,
	"X" to 10,
	"V" to 5,
	"IV" to 4,
	"I" to 1
)

fun formatRomanNumerals(n: Int): String = buildString {
	var remainder = n
	for ((numeral, divisor) in romanNumerals) {
		while (remainder >= divisor) {
			this.append(numeral)
			remainder -= divisor
		}
	}
}

fun effectTooltip(instance: MobEffectInstance, durationFactor: Float, ticksPerSecond: Float): MutableComponent {
	var mutableComponent = Component.translatable(instance.descriptionId)
	if (instance.amplifier > 0) mutableComponent = Component.translatable(
		"potion.withAmplifier",
		mutableComponent,
		Component.literal(formatRomanNumerals(instance.amplifier))
	)

	if (!instance.endsWithin(20)) mutableComponent = Component.translatable(
		"potion.withDuration",
		mutableComponent,
		MobEffectUtil.formatDuration(instance, durationFactor, ticksPerSecond)
	)

	return mutableComponent.withStyle(instance.effect.value().category.tooltipFormatting)
}

// Codec shenanigans
@Suppress("ConvertLambdaToReference")
fun <T : ByteBuf, V> StreamCodec<T, V>.toNonNullList(): StreamCodec<T, NonNullList<V>> =
	this.apply(ByteBufCodecs.collection { NonNullList.createWithCapacity(it) })

fun <T : ByteBuf, V> StreamCodec<T, V>.toMutableList(): StreamCodec<T, MutableList<V>> =
	this.apply(ByteBufCodecs.collection { NonNullList.createWithCapacity<V>(it).toMutableList() })

fun <T> List<T>.toNonNullList(): NonNullList<T> = NonNullList.copyOf(this)
fun <T : ByteBuf, V> StreamCodec<T, V>.toList(): StreamCodec<T, List<V>> = this.apply(ByteBufCodecs.list())

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

// Note the up and down cases are probably not very accurate.
fun directionalTargetFaceSection(
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
	UP    -> targetFaceSection(targetPos.x, targetPos.z, minxXNorthEast, minY, maxXNorthEast, maxY)
	DOWN  -> targetFaceSection(targetPos.x, targetPos.z, minxXSouthWest, minY, minxXSouthWest, maxY)
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

fun CompoundTag.putBlockState(key: String, value: BlockState) {
	this.put(key, BlockState.CODEC.encodeStart(NbtOps.INSTANCE, value).result().get())
}

fun CompoundTag.getBlockState(key: String): BlockState =
	BlockState.CODEC.decode(NbtOps.INSTANCE, this.get(key)).result().getOrNull()?.first
		?: Blocks.AIR.defaultBlockState()

fun <T : RecipeInput> CompoundTag.putRecipe(key: String, value: Recipe<T>) =
	this.put(key, Recipe.CODEC.encodeStart(NbtOps.INSTANCE, value).result().get())

@Suppress("UNCHECKED_CAST")
fun <I : RecipeInput, R : Recipe<I>> CompoundTag.getRecipe(key: String): R? =
	Recipe.CODEC.decode(NbtOps.INSTANCE, this.get(key)).result().getOrNull() as? R

fun CompoundTag.putEntity(key: String, value: Entity?): CompoundTag {
	if (value == null) return CompoundTag()
	this.put(key, CompoundTag().also { rootTag ->
		rootTag.putBoolean("isLivingEntity", value is LivingEntity)
		rootTag.putString("type", value.type.toString())
		if (value is LivingEntity) {
			rootTag.putDouble("maxHealth", value.maxHealth.toDouble())
			rootTag.putFloat("health", value.health)
		}
	})
	return this
}

@Suppress("ConvertLambdaToReference")
fun CompoundTag.createEntity(level: Level): Entity {
	val default = Pig(EntityType.PIG, level)
	if (!this.contains("type")) return default
	val typeString = this.getString("type").substringAfterLast('.')
	val type = BuiltInRegistries.ENTITY_TYPE.get(EntityType.byString(typeString).getOrNull()?.let {
		EntityType.getKey(it)
	})
	val entity = if (this.getBoolean("isLivingEntity")) type.create(level) as LivingEntity
	else type.create(level) ?: default

	if (entity is LivingEntity) {
		entity.setAttribute(Attributes.MAX_HEALTH, this.getDouble("maxHealth"))
		entity.health = this.getFloat("health")
	}
	return entity
}

fun LivingEntity.setAttribute(attribute: Holder<Attribute>, value: Double) {
	this.attributes.getInstance(attribute)?.let { it.baseValue = value }
}

fun LivingEntity.getAttributeInstance(attribute: Holder<Attribute>): AttributeInstance =
	this.attributes.getInstance(attribute) ?: throw NullPointerException()
/// !!! NOTICE !!! ///
// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good Javadoc for them!
/// INTERNAL DEFINITIONS FOLLOW ///