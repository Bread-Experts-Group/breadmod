package org.bread_experts_group.breadmod.experimental.recipe.recipe

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import java.util.function.Function

typealias StreamByteBufCodec<T> = StreamCodec<RegistryFriendlyByteBuf, T>
typealias NotNullListCodec<T> = StreamCodec<RegistryFriendlyByteBuf, NonNullList<T>>
typealias KListCodec<T> = StreamCodec<RegistryFriendlyByteBuf, List<T>>

// todo look into more efficient codec practices such as Codec#pair
//  also figure out more efficient ways of writing these methods
//  https://docs.neoforged.net/docs/datastorage/codecs
//  https://docs.neoforged.net/docs/networking/streamcodecs
abstract class BMRecipeSerializer<T : Recipe<*>> : RecipeSerializer<T> {
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

	/**
	 * Applies a [NonNullList] to the specified [StreamCodec]
	 */
	@Suppress("ConvertLambdaToReference")
	fun <T> nonNullListStreamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>): NotNullListCodec<T> =
		streamCodec.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity(cap) })

	/**
	 * [nonNullListStreamCodec] with [NonNullList] converted to a [MutableList]
	 */
	fun <T> listStreamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>): KListCodec<T> =
		streamCodec.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity<T>(cap).toMutableList() })

	fun <T> StreamByteBufCodec<T>.toMutableList(): StreamByteBufCodec<MutableList<T>> =
		this.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity<T>(cap).toMutableList() })
}