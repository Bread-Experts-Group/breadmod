package breadmod.util

import breadmod.ModMain
import breadmod.natives.windows.ACrasherWindows
import breadmod.util.RaycastResult.RaycastResultType
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender
import net.minecraft.data.tags.TagsProvider
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.LiteralContents
import net.minecraft.network.chat.contents.TranslatableContents
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
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
import net.minecraftforge.server.ServerLifecycleHooks
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.times
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i
import kotlin.system.exitProcess

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
 * Reads a value from an [IForgeRegistry] of types [T] indexed by the provided [JsonObject].
 * @return The value inside [IForgeRegistry] indexed by [JsonObject] (a JSON string).
 * @param p0 The [JsonObject] (a JSON string) to index the [IForgeRegistry] with.
 * @param T The type of the [IForgeRegistry] to read from.
 * @throws NullPointerException If the registry entry provided by [p0] is not found.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun <T> IForgeRegistry<T>.getFromJson(p0: JsonObject): T = ResourceLocation(p0.get(ENTRY_ID).asString).let {
    this.getValue(it) ?: throw NullPointerException("Registry entry not found: $it")
}

/**
 * Safely reads an integer from a potentially null [JsonElement].
 * If the [JsonElement] is null, or not a primitive, 0 is returned.
 * @return The integer value of this [JsonElement], or 0 if it is null or not a primitive.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun JsonElement?.readIntSafe(): Int = this.let { if (it?.isJsonPrimitive == true) it.asInt else 0 }

/**
 * Reads a [List] of [FluidStack]s from this [JsonArray].
 * @return The [List] of [FluidStack]s contained by this [JsonArray].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see jsonifyFluidList
 */
fun JsonArray.extractJsonFluidList(): List<FluidStack> = this.map { entryObject ->
    val entry = entryObject.asJsonObject
    FluidStack(ForgeRegistries.FLUIDS.getFromJson(entry), entry.get("amount").readIntSafe())
}

/**
 * Writes a [List] of [FluidStack]s to the provided [JsonArray].
 *
 * This procedure is additive; any contents already within the [JsonArray] are not overwritten.
 * @return The [JsonArray] containing the [List] of [FluidStack]s.
 * @param into The [JsonArray] to write the [List] of [FluidStack]s into.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see extractJsonFluidList
 */
fun List<FluidStack>.jsonifyFluidList(into: JsonArray = JsonArray(), propertyName: String = ENTRY_ID): JsonArray = into.also {
    this.forEach { stack ->
        it.add(JsonObject().also { obj ->
            obj.addProperty(propertyName, ForgeRegistries.FLUIDS.getKey(stack.fluid).toString())
            if (stack.amount > 1) obj.addProperty("amount", stack.amount)
        })
    }
}

/**
 * Reads a [List] of [ItemStack]s from this [JsonArray].
 * @return The [List] of [ItemStack]s contained by this [JsonArray].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see jsonifyItemList
 */
fun JsonArray.extractJsonItemList(): List<ItemStack> = this.map { entryObject ->
    val entry = entryObject.asJsonObject
    ItemStack(ForgeRegistries.ITEMS.getFromJson(entry), entry.get("amount").readIntSafe())
}

/**
 * Writes a [List] of [ItemStack]s to the provided [JsonArray].
 *
 * This procedure is additive; any contents already within the [JsonArray] are not overwritten.
 * @return The [JsonArray] containing the [List] of [ItemStack]s.
 * @param into The [JsonArray] to write the [List] of [ItemStack]s into.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see extractJsonItemList
 */
fun List<ItemStack>.jsonifyItemList(into: JsonArray = JsonArray(), propertyName: String = ENTRY_ID): JsonArray = into.also {
    this.forEach { stack ->
        it.add(JsonObject().also { obj ->
            obj.addProperty(propertyName, ForgeRegistries.ITEMS.getKey(stack.item).toString())
            if (stack.count > 1) obj.addProperty("amount", stack.count)
        })
    }
}

/**
 * Reads a [List] of [Pair]s containing a [TagKey] of type [T] (associated with an [IForgeRegistry])
 * with an integer (the count) from this [JsonArray].
 * @return The read [List] of [Pair]s.
 * @param registry The associated [IForgeRegistry] of type [T] for the [TagKey]s.
 * @param T The type the [IForgeRegistry] and [TagKey]s represent.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see jsonifyTagList
 */
fun <T> JsonArray.extractJsonTagList(registry: IForgeRegistry<T>): List<Pair<TagKey<T>, Int>> =
    this.map { entryObject ->
        val entry = entryObject.asJsonObject
        registry.createTagKey(entry.get(ENTRY_ID).asString) to entry.get("amount").readIntSafe()
    }

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
fun List<Pair<TagKey<*>, Int>>.jsonifyTagList(into: JsonArray = JsonArray(), propertyName: String = ENTRY_ID): JsonArray =
    into.also {
        this.forEach { (tag, count) ->
            it.add(JsonObject().also { obj ->
                obj.addProperty(propertyName, tag.location.toString())
                if (count > 1) obj.addProperty("amount", count)
            })
        }
    }

/**
 * Creates a [TagKey] from pertaining to this [IForgeRegistry].
 * @return The [TagKey] for this [IForgeRegistry], pointing to [path].
 * @param path The path this tag represents.
 * @param T The type of the [IForgeRegistry] this [TagKey] is for.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun <T> IForgeRegistry<T>.createTagKey(path: String): TagKey<T> = TagKey.create(this.registryKey, ResourceLocation(path))

/**
 * Reads a [List] of [FluidStack]s from this [FriendlyByteBuf].
 * @return The [List] of [FluidStack]s read off from this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see writeFluidList
 */
fun FriendlyByteBuf.readFluidList(): List<FluidStack> = List(this.readInt()) { this.readFluidStack() }

/**
 * Writes a [List] of [FluidStack]s to this [FriendlyByteBuf].
 * @return This [FriendlyByteBuf].
 * @param fluidList The [List] of [FluidStack]s to write to this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see readFluidList
 */
fun FriendlyByteBuf.writeFluidList(fluidList: List<FluidStack>): FriendlyByteBuf = this.also {
    this.writeInt(fluidList.size); fluidList.forEach { this.writeFluidStack(it) }
}

/**
 * Reads a [List] of [ItemStack]s from this [FriendlyByteBuf].
 * @return The [List] of [ItemStack]s read off from this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see writeItemList
 */
fun FriendlyByteBuf.readItemList(): List<ItemStack> = List(this.readInt()) { this.readItem() }

/**
 * Writes a [List] of [ItemStack]s to this [FriendlyByteBuf].
 * @return This [FriendlyByteBuf].
 * @param itemList The [List] of [ItemStack]s to write to this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see readItemList
 */
fun FriendlyByteBuf.writeItemList(itemList: List<ItemStack>) {
    this.writeInt(itemList.size); itemList.forEach { this.writeItem(it) }
}

/**
 * Reads a [List] of [Pair]s containing a representative [TagKey] of a type [T] of an [IForgeRegistry]
 * with an integer (the count) from this [FriendlyByteBuf].
 * @return The [List] of [Pair]s containing a [TagKey] and integer.
 * @param registry The [IForgeRegistry] the [TagKey]s belong to.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see writeTagList
 */
fun <T> FriendlyByteBuf.readTagList(registry: IForgeRegistry<T>): List<Pair<TagKey<T>, Int>> =
    List(this.readInt()) { registry.createTagKey(this.readUtf()) to this.readInt() }

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
 * Writes this [Collection] of [ItemStack]s into a [CompoundTag]; each [ItemStack] is indexed by its index in the [Collection].
 * @return The [CompoundTag] containing the serialized [Collection] of [ItemStack]s.
 * @param tag The [CompoundTag] to write into.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see deserialize
 */
fun Collection<ItemStack>.serialize(tag: CompoundTag): CompoundTag = tag.also {
    this.forEachIndexed { index, stack -> tag.put(index.toString(), stack.serializeNBT()) }
}

/**
 * Writes this [Collection] of [ItemStack]s into a new [CompoundTag].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see serialize
 */
fun Collection<ItemStack>.serialize(): CompoundTag = this.serialize(CompoundTag())

/**
 * Deserializes a [Collection] of [ItemStack]s from a [CompoundTag], writing them to this [MutableList].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see serialize
 */
fun MutableList<ItemStack>.deserialize(tag: CompoundTag): Unit =
    tag.allKeys.forEach { this[it.toInt()] = ItemStack.of(tag.getCompound(it)) }

/**
 * Checks if this [Fluid] can be represented under the given [TagKey].
 * @param tag The [TagKey] to check against.
 * @return `true` if this [Fluid] is represented by the [TagKey], `false` otherwise.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun Fluid.isTag(tag: TagKey<Fluid>): Boolean = ForgeRegistries.FLUIDS.tags()?.getTag(tag)?.contains(this) ?: false

/**
 * The amount of [Fluid] of a type [fluid] there is in this [IFluidHandler].
 * @param fluid The [Fluid] to check for.
 * @return The amount of requisite [Fluid] in this [IFluidHandler].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun IFluidHandler.amount(fluid: Fluid): Int =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isSame(fluid) }
        .sumOf { it.amount }

/**
 * The amount of [Fluid] of a type [tag] there is in this [IFluidHandler].
 * @param tag The [TagKey] to check for.
 * @return The amount of requisite [Fluid] in this [IFluidHandler].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun IFluidHandler.amount(tag: TagKey<Fluid>): Int =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isTag(tag) }
        .sumOf { it.amount }

// todo function does not account for vanilla items that don't require get() or key, causing an error that uses these methods
// todo: JavaDoc
inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: RegistryObject<A>): IntrinsicTagAppender<T> =
    this.also { this.add(*toAdd.map { it.get() }.toTypedArray()) }

/**
 * Adds a list of [RegistryObject]s to this [IntrinsicTagAppender].
 * @return This [IntrinsicTagAppender].
 * @param toAdd The [RegistryObject]s to add to this [IntrinsicTagAppender].
 * @param T The type of this [IntrinsicTagAppender].
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
fun <T> IntrinsicTagAppender<T>.add(vararg toAdd: RegistryObject<T>): TagsProvider.TagAppender<T> =
    (this as TagsProvider.TagAppender<T>).add(*toAdd.map { it }.toTypedArray())

/**
 * Adds a list of [RegistryObject]s to this [TagsProvider.TagAppender].
 * @return This [TagsProvider.TagAppender].
 * @param toAdd The [RegistryObject]s to add to this [TagsProvider.TagAppender].
 * @param T The type of this [TagsProvider.TagAppender].
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
fun <T> TagsProvider.TagAppender<T>.add(vararg toAdd: RegistryObject<T>): TagsProvider.TagAppender<T> =
    this.also { this.add(*toAdd.map { it.key }.toTypedArray()) }

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
                val entities = this.getEntities(exclude, AABB.ofSize(position, 1.0, 1.0, 1.0))
                if (entities.size > 0) entities.forEach {
                    if (it.position().distanceTo(position) < 1.0) return Entity(
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
            ModMain.LOGGER.info("blockRaycast function parameters: $origin, $direction, $length, $countFluid")
            ModMain.LOGGER.info("blockRaycast: start loop")
            while (true) {
                val position = origin + (direction * distance)
                val state = this.getBlockState(BlockPos(position.toVec3i()))
                ModMain.LOGGER.info("blockRaycast state: $state")
                ModMain.LOGGER.info("blockRaycast position: $position")
                if (!state.isAir && (countFluid || state.fluidState.fluidType.isAir)) return Block(
                    state,
                    origin,
                    position,
                    direction
                )
                setBlockAndUpdate(BlockPos(position.toVec3i()), Blocks.WHITE_WOOL.defaultBlockState())
                if (distance > length) return null
                distance += 0.1
            }
        }
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

        is LiteralContents -> {
            it.addProperty("type", "literal")
            it.addProperty("text", contents.text)
        }

        else -> throw NotImplementedError("Unknown contents: ${contents::class.qualifiedName}")
    }
}

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
fun DeferredRegister<Block>.registerBlockItem(
    itemRegister: DeferredRegister<Item>,
    id: String,
    block: () -> Block,
    properties: Item.Properties
): RegistryObject<BlockItem> =
    this.register(id, block).let { itemRegister.register(id) { BlockItem(it.get(), properties) } }

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
fun DeferredRegister<Block>.registerBlockItem(
    itemRegister: DeferredRegister<Item>,
    id: String,
    block: () -> Block,
    item: (block: Block) -> BlockItem
): RegistryObject<BlockItem> =
    this.register(id, block).let { itemRegister.register(id) { item(it.get()) } }

/**
 * Registers a [name]d [RecipeType] under the requisite [DeferredRegister].
 * @param name The name of the [RecipeType] to register.
 * @return The [RegistryObject] containing the registered [RecipeType].
 * @param T The type of the [Recipe] this [RecipeType] is for.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
fun <T : Recipe<*>> DeferredRegister<RecipeType<*>>.registerType(name: String): RegistryObject<RecipeType<T>> =
    this.register(name) {
        object : RecipeType<T> {
            override fun toString(): String = name
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
        Direction.NORTH -> side.opposite
        Direction.SOUTH -> side
        Direction.EAST -> side.clockWise
        Direction.WEST -> side.counterClockWise
        else -> translateFor
    }

object CompoundTagSerializer : KSerializer<CompoundTag> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "CompoundTag",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): CompoundTag = CompoundTag().also {
        repeat(decoder.decodeInt()) { _ ->
            it.put(
                decoder.decodeString(),
                when (val id = decoder.decodeByte()) {
                    else -> throw TODO("$id HELP!!!")
                }
            )
        }
    }

    override fun serialize(encoder: Encoder, value: CompoundTag) {
        encoder.encodeInt(value.size())
        value.allKeys.forEach { key ->
            encoder.encodeString(key)
            val tag = value.get(key) ?: throw NullPointerException("No $key???")
            encoder.encodeByte(tag.id)
            encoder.encodeString(tag.asString)
        }
    }
}

object LevelSerializer : KSerializer<Level> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Level",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Level = ServerLifecycleHooks.getCurrentServer().getLevel(
        ResourceKey.create(Registries.DIMENSION, ResourceLocation(decoder.decodeString()))
    )!!

    override fun serialize(encoder: Encoder, value: Level) = encoder.encodeString(value.dimension().location().toString())
}

object EntitySerializer : KSerializer<Entity> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Entity",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Entity =
        (decoder.decodeSerializableValue(EntityTypeSerializer)
            .create(decoder.decodeSerializableValue(LevelSerializer))
            ?: throw NullPointerException("Entity not found."))
            .also { it.deserializeNBT(decoder.decodeSerializableValue(CompoundTagSerializer)) }

    override fun serialize(encoder: Encoder, value: Entity) {
        encoder.encodeSerializableValue(EntityTypeSerializer, value.type)
        encoder.encodeSerializableValue(LevelSerializer, value.level())
        encoder.encodeSerializableValue(CompoundTagSerializer, value.serializeNBT())
    }
}

object EntityTypeSerializer : KSerializer<EntityType<*>> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "EntityType",
        PrimitiveKind.STRING
    )

    // TODO, I don't like this. It's not very efficient.
    override fun deserialize(decoder: Decoder): EntityType<*> {
        val id = decoder.decodeString()
        return ForgeRegistries.ENTITY_TYPES.first { it.descriptionId == id }
    }

    override fun serialize(encoder: Encoder, value: EntityType<*>) {
        encoder.encodeString(value.descriptionId)
    }
}

object MobEffectSerializer : KSerializer<MobEffect> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "MobEffect",
        PrimitiveKind.STRING
    )

    // TODO, I don't like this. It's not very efficient.
    override fun deserialize(decoder: Decoder): MobEffect {
        val id = decoder.decodeString()
        return ForgeRegistries.MOB_EFFECTS.first { it.descriptionId == id }
    }

    override fun serialize(encoder: Encoder, value: MobEffect) {
        encoder.encodeString(value.descriptionId)
    }
}

/**
 * [kotlinx.serialization] implementation for [MobEffectInstance].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
object MobEffectInstanceSerializer : KSerializer<MobEffectInstance> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MobEffectInstance") {
        element("effect", MobEffectSerializer.descriptor)
        element<Int>("duration")
        element<Int>("amplifier")
    }

    override fun deserialize(decoder: Decoder): MobEffectInstance {
        require(decoder is JsonDecoder)
        val element = decoder.decodeJsonElement() as kotlinx.serialization.json.JsonObject
        return MobEffectInstance(
            decoder.json.decodeFromJsonElement(MobEffectSerializer, element["effect"]!!),
            decoder.json.decodeFromJsonElement(element["duration"]!!),
            decoder.json.decodeFromJsonElement(element["amplifier"]!!)
        )
    }

    override fun serialize(encoder: Encoder, value: MobEffectInstance) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(buildJsonObject {
            put("effect", json.encodeToJsonElement(MobEffectSerializer, value.effect))
            put("duration", json.encodeToJsonElement(value.duration))
            put("amplifier", json.encodeToJsonElement(value.amplifier))
        })
    }
}

/// !!! NOTICE !!! ///

// Definitions above this line are for public use by other mods, possibly even external ones!
// Make sure to write good java-doc for them!

/// INTERNAL DEFINITIONS FOLLOW ///

internal const val ENTRY_ID = "id"

internal val json = Json { prettyPrint = true }

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
}