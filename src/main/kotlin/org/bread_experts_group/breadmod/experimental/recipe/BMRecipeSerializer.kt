package org.bread_experts_group.breadmod.experimental.recipe

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
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import java.util.function.Function

abstract class BMRecipeSerializer<T : Recipe<*>> : RecipeSerializer<T> {
    fun <O> itemStackListCodecModule(
        field: String,
        getter: Function<O, List<ItemStack>>
    ): RecordCodecBuilder<O, MutableList<ItemStack>> =
        ItemStack.CODEC.listOf().fieldOf(field).forGetter(getter)

    fun <O> fluidStackListCodecModule(
        field: String,
        getter: Function<O, List<FluidStack>>
    ): RecordCodecBuilder<O, MutableList<FluidStack>> =
        FluidStack.CODEC.listOf().fieldOf(field).forGetter(getter)

    fun <O> intCodecModule(field: String, getter: Function<O, Int>): RecordCodecBuilder<O, Int> =
        Codec.INT.fieldOf(field).forGetter(getter)

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
                    DataResult.success(NonNullList.of(SizedFluidIngredient.of(FluidStack.EMPTY), *fluidArray))
                }, { result -> DataResult.success(result) }
            ).forGetter(getter)

    /**
     * Applies a [NonNullList] to the specified [StreamCodec]
     */
    fun <T> nonNullListStreamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>): StreamCodec<RegistryFriendlyByteBuf, NonNullList<T>> =
        streamCodec.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity(cap) })

    /**
     * [nonNullListStreamCodec] with [NonNullList] converted to [List]
     */
    fun <T> listStreamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>): StreamCodec<RegistryFriendlyByteBuf, List<T>> =
        streamCodec.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity<T>(cap).toList() })
}