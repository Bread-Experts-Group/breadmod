package bread.mod.breadmod.util

import com.google.gson.JsonArray
import net.minecraft.data.tags.IntrinsicHolderTagsProvider.IntrinsicTagAppender
import net.minecraft.data.tags.TagsProvider
import net.minecraft.data.tags.TagsProvider.TagAppender
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.phys.Vec3

/**
 * Reads a [List] of [FluidStack]s from this [JsonArray].
 * @return The [List] of [FluidStack]s contained by this [JsonArray].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see jsonifyFluidList
 */
//fun JsonArray.extractJsonFluidList(): List<FluidStack> = this.map { entryObject ->
//    val entry = entryObject.asJsonObject
//    FluidStack(ForgeRegistries.FLUIDS.getFromJson(entry), entry.get("amount").readIntSafe())
//}

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
//fun List<FluidStack>.jsonifyFluidList(into: JsonArray = JsonArray(), propertyName: String = ENTRY_ID): JsonArray =
//    into.also {
//        this.forEach { stack ->
//            it.add(JsonObject().also { obj ->
//                obj.addProperty(propertyName, ForgeRegistries.FLUIDS.getKey(stack.fluid).toString())
//                if (stack.amount > 1) obj.addProperty("amount", stack.amount)
//            })
//        }
//    }

/**
 * Reads a [List] of [ItemStack]s from this [JsonArray].
 * @return The [List] of [ItemStack]s contained by this [JsonArray].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see jsonifyItemList
 */
//fun JsonArray.extractJsonItemList(): List<ItemStack> = this.map { entryObject ->
//    val entry = entryObject.asJsonObject
//    ItemStack(ForgeRegistries.ITEMS.getFromJson(entry), entry.get("amount").readIntSafe())
//}

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
//fun List<ItemStack>.jsonifyItemList(into: JsonArray = JsonArray(), propertyName: String = ENTRY_ID): JsonArray =
//    into.also {
//        this.forEach { stack ->
//            it.add(JsonObject().also { obj ->
//                obj.addProperty(propertyName, ForgeRegistries.ITEMS.getKey(stack.item).toString())
//                if (stack.count > 1) obj.addProperty("amount", stack.count)
//            })
//        }
//    }

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
//fun <T> JsonArray.extractJsonTagList(registry: IForgeRegistry<T>): List<Pair<TagKey<T>, Int>> =
//    this.map { entryObject ->
//        val entry = entryObject.asJsonObject
//        registry.createTagKey(entry.get(ENTRY_ID).asString) to entry.get("amount").readIntSafe()
//    }

/**
 * Creates a [TagKey] from pertaining to this [IForgeRegistry].
 * @return The [TagKey] for this [IForgeRegistry], pointing to [path].
 * @param path The path this tag represents.
 * @param T The type of the [IForgeRegistry] this [TagKey] is for.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
//fun <T> IForgeRegistry<T>.createTagKey(path: String): TagKey<T> =
//    TagKey.create(this.registryKey, ResourceLocation(path))

/**
 * Reads a [List] of [FluidStack]s from this [FriendlyByteBuf].
 * @return The [List] of [FluidStack]s read off from this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see writeFluidList
 */
//fun FriendlyByteBuf.readFluidList(): List<FluidStack> = List(this.readInt()) { this.readFluidStack() }

/**
 * Writes a [List] of [FluidStack]s to this [FriendlyByteBuf].
 * @return This [FriendlyByteBuf].
 * @param fluidList The [List] of [FluidStack]s to write to this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see readFluidList
 */
//fun FriendlyByteBuf.writeFluidList(fluidList: List<FluidStack>): FriendlyByteBuf = this.also {
//    this.writeInt(fluidList.size); fluidList.forEach { this.writeFluidStack(it) }
//}

/**
 * Reads a [List] of [ItemStack]s from this [FriendlyByteBuf].
 * @return The [List] of [ItemStack]s read off from this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see writeItemList
 */
//fun FriendlyByteBuf.readItemList(): List<ItemStack> = List(this.readInt()) { this.readItem() }

/**
 * Writes a [List] of [ItemStack]s to this [FriendlyByteBuf].
 * @return This [FriendlyByteBuf].
 * @param itemList The [List] of [ItemStack]s to write to this [FriendlyByteBuf].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see readItemList
 */
//fun FriendlyByteBuf.writeItemList(itemList: List<ItemStack>) {
//    this.writeInt(itemList.size); itemList.forEach { this.writeItem(it) }
//}

/**
 * Reads a [List] of [Pair]s containing a representative [TagKey] of a type [T] of an [IForgeRegistry]
 * with an integer (the count) from this [FriendlyByteBuf].
 * @return The [List] of [Pair]s containing a [TagKey] and integer.
 * @param registry The [IForgeRegistry] the [TagKey]s belong to.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see writeTagList
 */
//fun <T> FriendlyByteBuf.readTagList(registry: IForgeRegistry<T>): List<Pair<TagKey<T>, Int>> =
//    List(this.readInt()) { registry.createTagKey(this.readUtf()) to this.readInt() }

/**
 * Writes this [Collection] of [ItemStack]s into a [CompoundTag]; each [ItemStack] is indexed by its index in the [Collection].
 * @return The [CompoundTag] containing the serialized [Collection] of [ItemStack]s.
 * @param tag The [CompoundTag] to write into.
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see deserialize
 */
/*fun Collection<ItemStack>.serialize(tag: CompoundTag): CompoundTag = tag.also {
    this.forEachIndexed { index, stack -> tag.put(index.toString(), stack.serializeNBT()) }
}*/

/**
 * Writes this [Collection] of [ItemStack]s into a new [CompoundTag].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see serialize
 */
//fun Collection<ItemStack>.serialize(): CompoundTag = this.serialize(CompoundTag())

/**
 * Deserializes a [Collection] of [ItemStack]s from a [CompoundTag], writing them to this [MutableList].
 * @author Miko Elbrecht
 * @since 1.0.0
 * @see serialize
 */
/*fun MutableList<ItemStack>.deserialize(tag: CompoundTag): Unit =
    tag.allKeys.forEach { this[it.toInt()] = ItemStack.of(tag.getCompound(it)) }*/

/**
 * Checks if this [Fluid] can be represented under the given [TagKey].
 * @param tag The [TagKey] to check against.
 * @return `true` if this [Fluid] is represented by the [TagKey], `false` otherwise.
 * @author Miko Elbrecht
 * @since 1.0.0
 */
//fun Fluid.isTag(tag: TagKey<Fluid>): Boolean = ForgeRegistries.FLUIDS.tags()?.getTag(tag)?.contains(this) ?: false

/**
 * The amount of [Fluid] of a type [fluid] there is in this [IFluidHandler].
 * @param fluid The [Fluid] to check for.
 * @return The amount of requisite [Fluid] in this [IFluidHandler].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*fun IFluidHandler.amount(fluid: Fluid): Int =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isSame(fluid) }
        .sumOf { it.amount }*/

/**
 * The amount of [Fluid] of a type [tag] there is in this [IFluidHandler].
 * @param tag The [TagKey] to check for.
 * @return The amount of requisite [Fluid] in this [IFluidHandler].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*fun IFluidHandler.amount(tag: TagKey<Fluid>): Int =
    List(this.tanks) { this.getFluidInTank(it) }
        .filter { !it.isEmpty && it.fluid.isTag(tag) }
        .sumOf { it.amount }*/

// todo function does not account for vanilla items that don't require get() or key, causing an error that uses these methods
// todo: JavaDoc
/*inline fun <T, reified A : T> IntrinsicTagAppender<T>.add(vararg toAdd: RegistryObject<A>): IntrinsicTagAppender<T> =
    this.also { this.add(*toAdd.map { it.get() }.toTypedArray()) }*/

/**
 * Adds a list of [RegistryObject]s to this [IntrinsicTagAppender].
 * @return This [IntrinsicTagAppender].
 * @param toAdd The [RegistryObject]s to add to this [IntrinsicTagAppender].
 * @param T The type of this [IntrinsicTagAppender].
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
/*
fun <T> IntrinsicTagAppender<T>.add(vararg toAdd: RegistryObject<T>): TagsProvider.TagAppender<T> =
    (this as TagsProvider.TagAppender<T>).add(*toAdd.map { it }.toTypedArray())
*/

/**
 * Adds a list of [RegistryObject]s to this [TagsProvider.TagAppender].
 * @return This [TagsProvider.TagAppender].
 * @param toAdd The [RegistryObject]s to add to this [TagsProvider.TagAppender].
 * @param T The type of this [TagsProvider.TagAppender].
 * @author Miko Elbrecht, Logan McLean
 * @since 1.0.0
 */
/*fun <T> TagsProvider.TagAppender<T>.add(vararg toAdd: RegistryObject<T>): TagsProvider.TagAppender<T> =
    this.also { this.add(*toAdd.map { it.key }.toTypedArray()) }*/

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
        /*        fun Level.entityRaycast(
                    exclude: net.minecraft.world.entity.Entity?,
                    origin: Vec3,
                    direction: Vec3,
                    length: Double
                ): Entity? {
                    var distance = 0.0
                    while (true) {
                        val position = origin + (direction * distance)
                        val entities = this.getEntities(exclude, AABB.ofSize(position, 10.0, 10.0, 10.0))
                        if (entities.size > 0) entities.forEach {
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
                }*/

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
        /*        fun Level.blockRaycast(
                    origin: Vec3,
                    direction: Vec3,
                    length: Double,
                    countFluid: Boolean
                ): Block? {
                    var distance = 0.0
                    while (true) {
                        val position = origin + (direction * distance)
                        val state = this.getBlockState(BlockPos(position.toVec3i()))
                        if (!state.isAir && (countFluid || state.fluidState.fluidType.isAir)) return Block(
                            state,
                            origin,
                            position,
                            direction
                        )
                        if (distance > length) return null
                        distance += 0.1
                    }
                }
            }*/
    }
}

/*object CompoundTagSerializer : KSerializer<CompoundTag> {
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
}*/

/*object LevelSerializer : KSerializer<Level> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        "Level",
        PrimitiveKind.STRING
    )

    override fun deserialize(decoder: Decoder): Level = ServerLifecycleHooks.getCurrentServer().getLevel(
        ResourceKey.create(Registries.DIMENSION, ResourceLocation(decoder.decodeString()))
    )!!

    override fun serialize(encoder: Encoder, value: Level): Unit =
        encoder.encodeString(value.dimension().location().toString())
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
}*/

/*object EntityTypeSerializer : KSerializer<EntityType<*>> {
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
}*/

/**
 * [kotlinx.serialization] implementation for [MobEffectInstance].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
/*object MobEffectInstanceSerializer : KSerializer<MobEffectInstance> {
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
}*/