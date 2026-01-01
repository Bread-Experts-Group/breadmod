package org.bread_experts_group.breadmod.network

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.JsonOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.component.TypedDataComponent
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.codec.StreamDecoder
import net.minecraft.network.codec.StreamEncoder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.connection.ConnectionType
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import org.bread_experts_group.breadmod.util.ofOptional
import org.joml.Quaternionf
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Optional
import java.util.function.IntFunction
import kotlin.jvm.optionals.getOrNull

object BreadModCodecs {
	fun <T, S, R> ((T) -> R).compose(from: (S) -> T): (S) -> R = { this(from(it)) }

	fun <B : ByteBuf, L, R> pairStreamCodec(
		left: StreamCodec<B, L>,
		right: StreamCodec<B, R>
	): StreamCodec<B, Pair<L, R>> = object : StreamCodec<B, Pair<L, R>> {
		override fun decode(buffer: B): Pair<L, R> = left.decode(buffer) to right.decode(buffer)

		override fun encode(buffer: B, value: Pair<L, R>) {
			left.encode(buffer, value.first ?: throw NullPointerException("first cannot be null."))
			right.encode(buffer, value.second ?: throw NullPointerException("second cannot be null."))
		}
	}

	fun <F, S> kotlinPair(first: Codec<F>, second: Codec<S>): KotlinPairCodec<F, S> = KotlinPairCodec(first, second)

	fun <K, V, M : MutableMap<K, V>> RegistryFriendlyByteBuf.readMapRegFriendly(
		mapFactory: IntFunction<M>,
		keyReader: StreamDecoder<in RegistryFriendlyByteBuf, K>,
		valueReader: StreamDecoder<in RegistryFriendlyByteBuf, V>
	): M {
		val i: Int = this.readVarInt()
		val map = mapFactory.apply(i)

		repeat(i) {
			val k = keyReader.decode(this)
			val v = valueReader.decode(this)
			map[k] = v
		}

		return map
	}

	fun <K, V> RegistryFriendlyByteBuf.writeMapRegFriendly(
		map: MutableMap<K, V>,
		keyWriter: StreamEncoder<in RegistryFriendlyByteBuf, K>,
		valueWriter: StreamEncoder<in RegistryFriendlyByteBuf, V>
	) {
		this.writeVarInt(map.size)
		map.forEach { (key: K, value: V) ->
			keyWriter.encode(this, key ?: throw NullPointerException())
			valueWriter.encode(this, value ?: throw NullPointerException())
		}
	}

	inline fun <reified B : ByteBuf, V> testCodec(
		codec: Codec<V>?,
		streamCodec: StreamCodec<B, V>?,
		value: V,
		level: Level? = null
	) {
		val logger = LogManager.getLogger("BreadModCodecs - Tester")

		if (codec != null) {
			var encoded: DataResult<JsonElement>? = null

			try {
				logger.info("[Codec / Encode] Attempting to encode value[$value]")
				encoded = codec.encodeStart(JsonOps.INSTANCE, value)

				encoded.ifSuccess { result ->
					logger.info("[Codec / Encode] Encoded value successfully")
					logger.info("[Codec / Encode] Result: $result")
				}
				encoded.ifError { error ->
					logger.error("[Codec / Encode] Failed encoding value")
					logger.error(error)
				}
			} catch (e: Exception) {
				logger.error("[Codec / Encode] Exception in encoding value")
				e.printStackTrace()
			}

			try {
				logger.info("[Codec / Decode] Attempting to decode value[$value]")
				if (encoded != null && encoded.isSuccess) {
					val decoded = codec.parse(JsonOps.INSTANCE, encoded.orThrow)

					decoded.ifSuccess { result ->
						logger.info("[Codec / Decode] Decoded value successfully")
						logger.info("[Codec / Decode] Decode result: $result")
					}
					decoded.ifError { error ->
						logger.error("[Codec / Decode] Failed decoding value")
						logger.info("[Codec / Decode] Error: $error")
					}
				}
			} catch (e: Exception) {
				logger.error("[Codec / Decode] Exception in decoding value[$value]")
				e.printStackTrace()
			}
		}

		if (streamCodec != null && (level != null || B::class !is RegistryFriendlyByteBuf)) {
			val byteBuf = Unpooled.buffer()
			val buffer: B = when (B::class) {
				ByteBuf::class -> byteBuf
				FriendlyByteBuf::class -> FriendlyByteBuf(byteBuf)
				RegistryFriendlyByteBuf::class -> RegistryFriendlyByteBuf(
					byteBuf,
					(level ?: return).registryAccess(),
					ConnectionType.NEOFORGE
				)
				else -> throw IllegalArgumentException("[StreamCodec / Buffer] invalid buffer: ${B::class.simpleName}")
			} as B

			try {
				logger.info("[StreamCodec / Encode] Attempting to encode value[$value]")
				streamCodec.encode(buffer, value ?: return)
				logger.info("[StreamCodec / Encode] Encoded value successfully")
			} catch (e: Exception) {
				logger.error("[StreamCodec / Encode] Exception in encoding value")
				e.printStackTrace()
			}

			try {
				logger.info("[StreamCodec / Decode] Attempting to decode value")
				val decoded = streamCodec.decode(buffer)
				logger.info("[StreamCodec / Decode] Decoded value successfully")
				logger.info("[StreamCodec / Decode] Decode result: $decoded")
			} catch (e: Exception) {
				logger.error("[StreamCodec / Decode] Exception in decoding value")
				e.printStackTrace()
			}

			logger.info("Releasing ByteBuf")
			buffer.release()
		} else logger.error("level is null, skipped StreamCodec tests uses RegistryFriendlyByteBuf")
	}

	val DATA_COMPONENT_MAP_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DataComponentMap> =
		object : StreamCodec<RegistryFriendlyByteBuf, DataComponentMap> {
			override fun decode(buffer: RegistryFriendlyByteBuf): DataComponentMap {
				val dataComponentMapBuilder = DataComponentMap.builder()
				val componentMap: MutableMap<Int, TypedDataComponent<*>> =
					buffer.readMapRegFriendly(
						{ _ -> mutableMapOf() },
						ByteBufCodecs.INT,
						TypedDataComponent.STREAM_CODEC
					)

				for (component in componentMap.values) dataComponentMapBuilder.set(
					{ component.type as DataComponentType<in Any> },
					component.value()
				)

				return dataComponentMapBuilder.build()
			}

			override fun encode(
				buffer: RegistryFriendlyByteBuf,
				value: DataComponentMap
			) {
				val componentMap: MutableMap<Int, TypedDataComponent<*>> = mutableMapOf()
				var index = 0
				for (type in value) componentMap[index++] = type
				buffer.writeMapRegFriendly(componentMap, ByteBufCodecs.INT, TypedDataComponent.STREAM_CODEC::encode)
			}
		}
	val FLUID_ID_SERIALIZER: (Fluid) -> String = { fluid: Fluid -> BuiltInRegistries.FLUID.getKey(fluid).toString() }
	val FLUID_ID_DESERIALIZER: (String) -> Fluid = { id: String ->
		BuiltInRegistries.FLUID.get(ResourceLocation.parse(id))
	}
	val ITEM_ID_SERIALIZER: (Item) -> String = { item: Item -> BuiltInRegistries.ITEM.getKey(item).toString() }
	val ITEM_ID_DESERIALIZER: (String) -> Item = { id: String ->
		BuiltInRegistries.ITEM.get(ResourceLocation.parse(id))
	}
	val TOOL_GUN_CODEC: Codec<ToolGunData> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				ResourceLocation.CODEC.fieldOf("id").forGetter(ToolGunData::id),
				CompoundTag.CODEC.fieldOf("extra_data").forGetter(ToolGunData::extraData),
				Codec.INT.fieldOf("index").forGetter(ToolGunData::modeIndex)
			).apply(instance, ::ToolGunData)
		}
	val TOOL_GUN_STREAM_CODEC: StreamCodec<FriendlyByteBuf, ToolGunData> = StreamCodec.composite(
		ResourceLocation.STREAM_CODEC, ToolGunData::id,
		ByteBufCodecs.TRUSTED_COMPOUND_TAG, ToolGunData::extraData,
		ByteBufCodecs.VAR_INT, ToolGunData::modeIndex,
		::ToolGunData
	)
	val BLOCKSTATE_STREAM_CODEC: StreamCodec<ByteBuf, BlockState> = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
	val BLOCK_MAP_STREAM_CODEC: StreamCodec<FriendlyByteBuf, Map<BlockPos, BlockState>> =
		object : StreamCodec<FriendlyByteBuf, Map<BlockPos, BlockState>> {
			override fun decode(buffer: FriendlyByteBuf): Map<BlockPos, BlockState> =
				buffer.readMap(BlockPos.STREAM_CODEC, this@BreadModCodecs.BLOCKSTATE_STREAM_CODEC)

			override fun encode(buffer: FriendlyByteBuf, value: Map<BlockPos, BlockState>) {
				buffer.writeMap(value, BlockPos.STREAM_CODEC, this@BreadModCodecs.BLOCKSTATE_STREAM_CODEC)
			}
		}
	val FLUIDSTATE_STREAM_CODEC: StreamCodec<ByteBuf, FluidState> = ByteBufCodecs.idMapper(Fluid.FLUID_STATE_REGISTRY)
	val FLUID_MAP_STREAM_CODEC: StreamCodec<FriendlyByteBuf, Map<BlockPos, FluidState>> =
		object : StreamCodec<FriendlyByteBuf, Map<BlockPos, FluidState>> {
			override fun decode(buffer: FriendlyByteBuf): Map<BlockPos, FluidState> =
				buffer.readMap(BlockPos.STREAM_CODEC, this@BreadModCodecs.FLUIDSTATE_STREAM_CODEC)

			override fun encode(buffer: FriendlyByteBuf, value: Map<BlockPos, FluidState>) {
				buffer.writeMap(value, BlockPos.STREAM_CODEC, this@BreadModCodecs.FLUIDSTATE_STREAM_CODEC)
			}
		}
	val BIG_DECIMAL_CODEC: PrimitiveCodec<BigDecimal> = object : PrimitiveCodec<BigDecimal> {
		override fun toString(): String = "BigDecimal"
		override fun <T> write(ops: DynamicOps<T>, value: BigDecimal): T = ops.createString(value.toString())
		override fun <T> read(ops: DynamicOps<T>, input: T): DataResult<BigDecimal> {
			when (input) {
				is StringTag -> return DataResult.success(BigDecimal(input.asString))
				is JsonPrimitive -> return DataResult.success(BigDecimal(input.asString))
			}
			return DataResult.error { "Not a string tag: $input [${if (input != null) input::class.qualifiedName else "?"}]" }
		}
	}
	val BIG_DECIMAL_STREAM_CODEC: StreamCodec<ByteBuf, BigDecimal> = object : StreamCodec<ByteBuf, BigDecimal> {
		override fun decode(buffer: ByteBuf): BigDecimal {
			val scale = buffer.readInt()
			val data = ByteArray(buffer.readInt())
			buffer.readBytes(data)
			val unscaled = BigInteger(data)
			return BigDecimal(unscaled, scale)
		}

		override fun encode(buffer: ByteBuf, value: BigDecimal) {
			buffer.writeInt(value.scale())
			val data = value.unscaledValue().toByteArray()
			buffer.writeInt(data.size)
			buffer.writeBytes(data)
		}
	}
	val U_LONG_CODEC: PrimitiveCodec<ULong> = object : PrimitiveCodec<ULong> {
		override fun toString(): String = "ULong"
		override fun <T> write(ops: DynamicOps<T>, value: ULong): T = ops.createLong(value.toLong())
		override fun <T> read(ops: DynamicOps<T>, input: T): DataResult<ULong> {
			when (input) {
				is LongTag -> return DataResult.success(input.asLong.toULong())
				is JsonPrimitive -> return DataResult.success(input.asLong.toULong())
			}
			return DataResult.error { "Not a string tag: $input [${if (input != null) input::class.qualifiedName else "?"}]" }
		}
	}
	val U_LONG_STREAM_CODEC: StreamCodec<ByteBuf, ULong> = object : StreamCodec<ByteBuf, ULong> {
		override fun decode(buffer: ByteBuf): ULong = buffer.readLong().toULong()
		override fun encode(buffer: ByteBuf, value: ULong) {
			buffer.writeLong(value.toLong())
		}
	}
	val BIG_DESCRIPTOR_ITEM_CODEC: Codec<BigDescriptor<Item>> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			Codec.STRING.fieldOf("id").forGetter { this.ITEM_ID_SERIALIZER(it.value) },
			this.BIG_DECIMAL_CODEC.fieldOf("count").forGetter(BigDescriptor<Item>::amount),
			DataComponentMap.CODEC.optionalFieldOf("components")
				.forGetter { Optional.ofNullable(if (it.components.isEmpty) null else it.components) }
		).apply(inst) { id: String, amount: BigDecimal, components ->
			BigDescriptor(amount, this.ITEM_ID_DESERIALIZER(id), components.getOrNull() ?: DataComponentMap.EMPTY)
		}
	}.codec()
	val BIG_DESCRIPTOR_ITEM_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BigDescriptor<Item>> =
		StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			{ this.ITEM_ID_SERIALIZER(it.value) },
			this.BIG_DECIMAL_STREAM_CODEC,
			BigDescriptor<Item>::amount,
			this.DATA_COMPONENT_MAP_STREAM_CODEC.ofOptional(),
			{ Optional.ofNullable(if (it.components.isEmpty) null else it.components) },
			{ id, amount, components: Optional<DataComponentMap> ->
				BigDescriptor(amount, this.ITEM_ID_DESERIALIZER(id), components.getOrNull() ?: DataComponentMap.EMPTY)
			}
		)
	val BIG_DESCRIPTOR_FLUID_CODEC: Codec<BigDescriptor<Fluid>> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			Codec.STRING.fieldOf("id").forGetter { this.FLUID_ID_SERIALIZER(it.value) },
			this.BIG_DECIMAL_CODEC.fieldOf("amount").forGetter(BigDescriptor<Fluid>::amount),
			DataComponentMap.CODEC.optionalFieldOf("components")
				.forGetter { Optional.ofNullable(if (it.components.isEmpty) null else it.components) }
		).apply(inst) { id: String, amount: BigDecimal, components: Optional<DataComponentMap> ->
			BigDescriptor(amount, this.FLUID_ID_DESERIALIZER(id), components.getOrNull() ?: DataComponentMap.EMPTY)
		}
	}.codec()
	val BIG_DESCRIPTOR_FLUID_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BigDescriptor<Fluid>> =
		StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8,
			{ this.FLUID_ID_SERIALIZER(it.value) },
			this.BIG_DECIMAL_STREAM_CODEC,
			BigDescriptor<Fluid>::amount,
			this.DATA_COMPONENT_MAP_STREAM_CODEC.ofOptional(),
			{ Optional.ofNullable(if (it.components.isEmpty) null else it.components) },
			{ id, amount, components ->
				BigDescriptor(amount, this.FLUID_ID_DESERIALIZER(id), components.getOrNull() ?: DataComponentMap.EMPTY)
			}
		)
	val RECIPE_HOLDER_CODEC: Codec<RecipeHolder<*>> = RecordCodecBuilder.create { instance ->
		instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(RecipeHolder<*>::id),
			Recipe.CODEC.fieldOf("recipe").forGetter(RecipeHolder<*>::value)
		).apply(instance) { id, value -> RecipeHolder(id, value) }
	}

	@Suppress("ConvertLambdaToReference") // necessary because of overload ambiguity.
	val VEC3_STREAM_CODEC: StreamCodec<ByteBuf, Vec3> = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, { it.x },
		ByteBufCodecs.DOUBLE, { it.y },
		ByteBufCodecs.DOUBLE, { it.z },
		::Vec3
	)
	val QUATERNIONF_STREAM_CODEC: StreamCodec<ByteBuf, Quaternionf> = StreamCodec.composite(
		ByteBufCodecs.FLOAT, { it.x },
		ByteBufCodecs.FLOAT, { it.y },
		ByteBufCodecs.FLOAT, { it.z },
		ByteBufCodecs.FLOAT, { it.w },
		::Quaternionf
	)
	val BOUNDING_BOX_STREAM_CODEC: StreamCodec<ByteBuf, BoundingBox> = StreamCodec.composite(
		ByteBufCodecs.INT, BoundingBox::minX,
		ByteBufCodecs.INT, BoundingBox::minY,
		ByteBufCodecs.INT, BoundingBox::minZ,
		ByteBufCodecs.INT, BoundingBox::maxX,
		ByteBufCodecs.INT, BoundingBox::maxY,
		ByteBufCodecs.INT, BoundingBox::maxZ,
		::BoundingBox
	)

//	// Convenience codec methods.
//	fun <O> itemStackListCodecModule(
//		field: String,
//		getter: Function<O, List<ItemStack>>
//	): RecordCodecBuilder<O, MutableList<ItemStack>> =
//		ItemStack.CODEC.listOf().fieldOf(field).forGetter(getter)
//
//	fun <O> optionalItemStackListCodecModule(
//		field: String,
//		getter: Function<O, List<ItemStack>>
//	): RecordCodecBuilder<O, MutableList<ItemStack>> =
//		ItemStack.CODEC.listOf().optionalFieldOf(field, listOf()).forGetter(getter)
//
//	fun <O> fluidStackListCodecModule(
//		field: String,
//		getter: Function<O, List<FluidStack>>
//	): RecordCodecBuilder<O, MutableList<FluidStack>> =
//		FluidStack.CODEC.listOf().fieldOf(field).forGetter(getter)
//
//	fun <O> optionalFluidStackListCodecModule(
//		field: String,
//		getter: Function<O, List<FluidStack>>
//	): RecordCodecBuilder<O, MutableList<FluidStack>> =
//		FluidStack.CODEC.listOf().optionalFieldOf(field, mutableListOf()).forGetter(getter)
//
//	fun <O> optionalIntCodecModule(
//		field: String,
//		getter: Function<O, Int?>
//	): RecordCodecBuilder<O, Int?> = Codec.INT.optionalFieldOf(field, 0).forGetter(getter)
//
//	fun <O> sizedIngredientCodecModule(
//		field: String,
//		getter: Function<O, List<SizedIngredient>>
//	): RecordCodecBuilder<O, List<SizedIngredient>> =
//		SizedIngredient.FLAT_CODEC
//			.listOf()
//			.optionalFieldOf(field, listOf())
//			.flatXmap(DataResult<O>::success, DataResult<O>::success)
//			.forGetter(getter)
//
//	fun <O> optionalSizedIngredientCodecModule(
//		field: String,
//		getter: Function<O, List<SizedIngredient>>
//	): RecordCodecBuilder<O, List<SizedIngredient>> =
//		SizedIngredient.FLAT_CODEC
//			.listOf()
//			.optionalFieldOf(field, listOf())
//			.flatXmap(DataResult<O>::success, DataResult<O>::success)
//			.forGetter(getter)
//
//	fun <O> sizedFluidIngredientCodecModule(
//		field: String,
//		getter: Function<O, List<SizedFluidIngredient>>
//	): RecordCodecBuilder<O, List<SizedFluidIngredient>> =
//		SizedFluidIngredient.FLAT_CODEC
//			.listOf()
//			.optionalFieldOf(field, listOf())
//			.flatXmap(DataResult<O>::success, DataResult<O>::success)
//			.forGetter(getter)
//
//	fun <O> optionalSizedFluidIngredientCodecModule(
//		field: String,
//		getter: Function<O, List<SizedFluidIngredient>>
//	): RecordCodecBuilder<O, List<SizedFluidIngredient>> =
//		SizedFluidIngredient.FLAT_CODEC
//			.listOf()
//			.optionalFieldOf(field, listOf())
//			.flatXmap(DataResult<O>::success, DataResult<O>::success)
//			.forGetter(getter)
}