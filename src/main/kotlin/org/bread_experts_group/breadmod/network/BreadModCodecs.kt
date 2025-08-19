package org.bread_experts_group.breadmod.network

import com.google.gson.JsonPrimitive
import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.DynamicOps
import com.mojang.serialization.codecs.PrimitiveCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentMap
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.StringTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeHolder
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.experimental.particle.ClosedSystem
import org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.BigDescriptor
import java.math.BigDecimal
import java.math.BigInteger

object BreadModCodecs {
	fun <T, S, R> ((T) -> R).compose(from: (S) -> T): (S) -> R = { this(from(it)) }

	// todo make sure this doesn't crash from using kotlin pairs instead of mojang pairs
	fun <B : ByteBuf, L, R> pairStreamCodec(
		left: StreamCodec<B, L>,
		right: StreamCodec<B, R>
	): StreamCodec<B, Pair<L, R>> = object : StreamCodec<B, Pair<L, R>> {
		override fun decode(buffer: B): Pair<L, R> = left.decode(buffer) to right.decode(buffer)

		override fun encode(buffer: B, value: Pair<L, R>) {
			left.encode(buffer, value.first!!)
			right.encode(buffer, value.second!!)
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
	val CLOSED_SYSTEM_CODEC: Codec<ClosedSystem> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				CompoundTag.CODEC.fieldOf("value").forGetter(ClosedSystem::toNBT)
			).apply(instance, ClosedSystem::createFromTag)
		}
	val CLOSED_SYSTEM_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ClosedSystem> = StreamCodec.composite(
		ByteBufCodecs.TRUSTED_COMPOUND_TAG,
		ClosedSystem::toNBT, ClosedSystem::createFromTag
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
			this.BIG_DECIMAL_CODEC.fieldOf("amount").forGetter(BigDescriptor<Item>::amount),
			DataComponentMap.CODEC.fieldOf("components").forGetter(BigDescriptor<Item>::components)
		).apply(inst) { id: String, amount: BigDecimal, components: DataComponentMap ->
			BigDescriptor(amount, this.ITEM_ID_DESERIALIZER(id), components)
		}
	}.codec()
	val BIG_DESCRIPTOR_ITEM_STREAM_CODEC: StreamCodec<ByteBuf, BigDescriptor<Item>> = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, { this.ITEM_ID_SERIALIZER(it.value) },
		this.BIG_DECIMAL_STREAM_CODEC, BigDescriptor<Item>::amount,
		{ id, amount -> BigDescriptor(amount, this.ITEM_ID_DESERIALIZER(id)) } // TODO COMPONENTS
	)
	val BIG_DESCRIPTOR_FLUID_CODEC: Codec<BigDescriptor<Fluid>> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			Codec.STRING.fieldOf("id").forGetter { this.FLUID_ID_SERIALIZER(it.value) },
			this.BIG_DECIMAL_CODEC.fieldOf("amount").forGetter(BigDescriptor<Fluid>::amount),
			DataComponentMap.CODEC.fieldOf("components").forGetter(BigDescriptor<Fluid>::components)
		).apply(inst) { id: String, amount: BigDecimal, components: DataComponentMap ->
			BigDescriptor(amount, this.FLUID_ID_DESERIALIZER(id), components)
		}
	}.codec()
	val BIG_DESCRIPTOR_FLUID_STREAM_CODEC: StreamCodec<ByteBuf, BigDescriptor<Fluid>> = StreamCodec.composite(
		ByteBufCodecs.STRING_UTF8, { this.FLUID_ID_SERIALIZER(it.value) },
		this.BIG_DECIMAL_STREAM_CODEC, BigDescriptor<Fluid>::amount,
		{ id, amount -> BigDescriptor(amount, this.FLUID_ID_DESERIALIZER(id)) } // TODO COMPONENTS
	)
	val RECIPE_HOLDER_CODEC: Codec<RecipeHolder<*>> = RecordCodecBuilder.create { instance ->
		instance.group(
			ResourceLocation.CODEC.fieldOf("id").forGetter(RecipeHolder<*>::id),
			Recipe.CODEC.fieldOf("recipe").forGetter(RecipeHolder<*>::value)
		).apply(instance) { id, value -> RecipeHolder(id, value) }
	}

	@Suppress("ConvertLambdaToReference") // necessary because of overload ambiguity.
	val VEC3: StreamCodec<ByteBuf, Vec3> = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, { it.x },
		ByteBufCodecs.DOUBLE, { it.y },
		ByteBufCodecs.DOUBLE, { it.z },
		::Vec3
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