package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.serialization.Codec
import com.mojang.serialization.DataResult
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.NonNullList
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.*
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import java.util.function.Function

class FluidEnergyRecipeSerializer : RecipeSerializer<FluidEnergyRecipe> {
    private val codec: MapCodec<FluidEnergyRecipe> = RecordCodecBuilder.mapCodec { inst ->
        inst.group(
            sizedIngredientCodecModule(FluidEnergyRecipe::itemIngredients),
            sizedFluidIngredientCodecModule(FluidEnergyRecipe::fluidIngredients),
            itemStackCodecModule(FluidEnergyRecipe::results),
            fluidStackCodecModule(FluidEnergyRecipe::fluidResults),
            intCodecModule("energy", FluidEnergyRecipe::energy),
            intCodecModule("time", FluidEnergyRecipe::time),
        ).apply(inst, ::FluidEnergyRecipe)
    }

    private val streamCodec: StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = StreamCodec.composite(
        nonNullListStreamCodec(SizedIngredient.STREAM_CODEC), FluidEnergyRecipe::itemIngredients,
        nonNullListStreamCodec(SizedFluidIngredient.STREAM_CODEC), FluidEnergyRecipe::fluidIngredients,
        ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::results,
        listStreamCodec(FluidStack.STREAM_CODEC), FluidEnergyRecipe::fluidResults,
        ByteBufCodecs.INT, FluidEnergyRecipe::energy,
        ByteBufCodecs.INT, FluidEnergyRecipe::time,
        ::FluidEnergyRecipe
    )

    private fun <O> sizedIngredientCodecModule(getter: Function<O, NonNullList<SizedIngredient>>) =
        SizedIngredient.FLAT_CODEC
            .listOf()
            .fieldOf("item_ingredients")
            .flatXmap(
                { itemList ->
                    val itemArray = itemList.toTypedArray()
                    DataResult.success(NonNullList.of(SizedIngredient.of(ItemStack.EMPTY.item, 0), *itemArray))
                }, { result -> DataResult.success(result) }
            ).forGetter(getter)

    private fun <O> sizedFluidIngredientCodecModule(getter: Function<O, NonNullList<SizedFluidIngredient>>) =
        SizedFluidIngredient.FLAT_CODEC
            .listOf()
            .fieldOf("fluid_ingredients")
            .flatXmap(
                { fluidList ->
                    val fluidArray = fluidList.toTypedArray()
                    DataResult.success(NonNullList.of(SizedFluidIngredient.of(FluidStack.EMPTY), *fluidArray))
                }, { result -> DataResult.success(result) }
            ).forGetter(getter)

    private fun <O> itemStackCodecModule(getter: Function<O, List<ItemStack>>) =
        ItemStack.CODEC.listOf().fieldOf("item_result").forGetter(getter)

    private fun <O> fluidStackCodecModule(getter: Function<O, List<FluidStack>>) =
        FluidStack.CODEC.listOf().fieldOf("fluid_result").forGetter(getter)

    private fun <O> intCodecModule(field: String, getter: Function<O, Int>) =
        Codec.INT.fieldOf(field).forGetter(getter)

    /**
     * Applies a [NonNullList] to the specified [StreamCodec]
     */
    private fun <T> nonNullListStreamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>) =
        streamCodec.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity(cap) })

    /**
     * [nonNullListStreamCodec] with [NonNullList] converted to [List]
     */
    private fun <T> listStreamCodec(streamCodec: StreamCodec<RegistryFriendlyByteBuf, T>) =
        streamCodec.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity<T>(cap).toList() })

    override fun codec(): MapCodec<FluidEnergyRecipe> = codec

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = streamCodec
}