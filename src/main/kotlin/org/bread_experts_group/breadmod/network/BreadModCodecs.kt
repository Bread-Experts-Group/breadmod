package org.bread_experts_group.breadmod.network

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.experimental.particle.ClosedSystem
import java.math.BigDecimal
import java.util.function.Function

object BreadModCodecs {
	val EXPANSIBLE_CODEC: Codec<BigDecimal> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				Codec.STRING.fieldOf("value").forGetter(BigDecimal::toEngineeringString)
			).apply(instance, ::BigDecimal)
		}
	val EXPANSIBLE_STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BigDecimal> =
		StreamCodec.composite(ByteBufCodecs.STRING_UTF8, BigDecimal::toEngineeringString, ::BigDecimal)
	val TOOL_GUN_CODEC: Codec<ToolGunData> =
		RecordCodecBuilder.create { instance ->
			instance.group(
				IToolGunMode.CODEC.fieldOf("mode").forGetter(ToolGunData::mode),
				CompoundTag.CODEC.fieldOf("extra_data").forGetter(ToolGunData::extraData),
				Codec.INT.fieldOf("index").forGetter(ToolGunData::modeIndex)
			).apply(instance, ::ToolGunData)
		}
	val TOOL_GUN_STREAM_CODEC: StreamCodec<FriendlyByteBuf, ToolGunData> = StreamCodec.composite(
		IToolGunMode.STREAM_CODEC, ToolGunData::mode,
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
	val BLOCK_MAP_STREAM_CODEC: StreamCodec<FriendlyByteBuf, Map<BlockPos, BlockState>> =
		object : StreamCodec<FriendlyByteBuf, Map<BlockPos, BlockState>> {
			private val stateCodec = ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
			override fun decode(buffer: FriendlyByteBuf): Map<BlockPos, BlockState> =
				buffer.readMap(BlockPos.STREAM_CODEC, this.stateCodec)

			override fun encode(buffer: FriendlyByteBuf, value: Map<BlockPos, BlockState>) {
				buffer.writeMap(value, BlockPos.STREAM_CODEC, this.stateCodec)
			}
		}
	val FLUID_MAP_STREAM_CODEC: StreamCodec<FriendlyByteBuf, Map<BlockPos, FluidState>> =
		object : StreamCodec<FriendlyByteBuf, Map<BlockPos, FluidState>> {
			private val stateCodec = ByteBufCodecs.idMapper(Fluid.FLUID_STATE_REGISTRY)
			override fun decode(buffer: FriendlyByteBuf): Map<BlockPos, FluidState> =
				buffer.readMap(BlockPos.STREAM_CODEC, this.stateCodec)

			override fun encode(buffer: FriendlyByteBuf, value: Map<BlockPos, FluidState>) {
				buffer.writeMap(value, BlockPos.STREAM_CODEC, this.stateCodec)
			}
		}

	@Suppress("ConvertLambdaToReference") // necessary because of overload ambiguity.
	val VEC3_STREAM_CODEC: StreamCodec<ByteBuf, Vec3> = StreamCodec.composite(
		ByteBufCodecs.DOUBLE, { it.x },
		ByteBufCodecs.DOUBLE, { it.y },
		ByteBufCodecs.DOUBLE, { it.z },
		::Vec3
	)

	// Convenience codec methods.
	fun <O> itemStackListCodecModule(
		field: String,
		getter: Function<O, List<ItemStack>>
	): RecordCodecBuilder<O, MutableList<ItemStack>> =
		ItemStack.CODEC.listOf().fieldOf(field).forGetter(getter)

	fun <O> optionalItemStackListCodecModule(
		field: String,
		getter: Function<O, List<ItemStack>>
	): RecordCodecBuilder<O, MutableList<ItemStack>> =
		ItemStack.CODEC.listOf().optionalFieldOf(field, listOf()).forGetter(getter)

	fun <O> fluidStackListCodecModule(
		field: String,
		getter: Function<O, List<FluidStack>>
	): RecordCodecBuilder<O, MutableList<FluidStack>> =
		FluidStack.CODEC.listOf().fieldOf(field).forGetter(getter)

	fun <O> optionalFluidStackListCodecModule(
		field: String,
		getter: Function<O, List<FluidStack>>
	): RecordCodecBuilder<O, MutableList<FluidStack>> =
		FluidStack.CODEC.listOf().optionalFieldOf(field, mutableListOf()).forGetter(getter)

	fun <O> optionalIntCodecModule(
		field: String,
		getter: Function<O, Int?>
	): RecordCodecBuilder<O, Int?> = Codec.INT.optionalFieldOf(field, 0).forGetter(getter)

	fun <O> sizedIngredientCodecModule(
		field: String,
		getter: Function<O, NonNullList<SizedIngredient>>
	): RecordCodecBuilder<O, NonNullList<SizedIngredient>> =
		SizedIngredient.FLAT_CODEC
			.listOf()
			.fieldOf(field)
			.flatXmap(
				{ itemList ->
					val itemArray = itemList.toTypedArray()
					DataResult.success(NonNullList.of(SizedIngredient.of(ItemStack.EMPTY.item, 1), *itemArray))
				}, { result -> DataResult.success(result) }
			).forGetter(getter)

	fun <O> optionalSizedIngredientCodecModule(
		field: String,
		getter: Function<O, NonNullList<SizedIngredient>>
	): RecordCodecBuilder<O, NonNullList<SizedIngredient>> =
		SizedIngredient.FLAT_CODEC
			.listOf()
			.optionalFieldOf(field, NonNullList.create())
			.flatXmap(
				{ itemList ->
					val itemArray = itemList.toTypedArray()
					DataResult.success(NonNullList.of(SizedIngredient.of(ItemStack.EMPTY.item, 1), *itemArray))
				}, { result -> DataResult.success(result) }
			).forGetter(getter)

	fun <O> sizedFluidIngredientCodecModule(
		field: String,
		getter: Function<O, NonNullList<SizedFluidIngredient>>
	): RecordCodecBuilder<O, NonNullList<SizedFluidIngredient>> =
		SizedFluidIngredient.FLAT_CODEC
			.listOf()
			.fieldOf(field)
			.flatXmap(
				{ fluidList ->
					val fluidArray = fluidList.toTypedArray()
					DataResult.success(NonNullList.of(SizedFluidIngredient.of(Fluids.WATER, 1), *fluidArray))
				}, { result -> DataResult.success(result) }
			).forGetter(getter)

	fun <O> optionalSizedFluidIngredientCodecModule(
		field: String,
		getter: Function<O, NonNullList<SizedFluidIngredient>>
	): RecordCodecBuilder<O, NonNullList<SizedFluidIngredient>> =
		SizedFluidIngredient.FLAT_CODEC
			.listOf()
			.optionalFieldOf(field, NonNullList.create())
			.flatXmap(
				{ fluidList ->
					val fluidArray = fluidList.toTypedArray()
					DataResult.success(NonNullList.of(SizedFluidIngredient.of(Fluids.WATER, 1), *fluidArray))
				}, { result -> DataResult.success(result) }
			).forGetter(getter)
}