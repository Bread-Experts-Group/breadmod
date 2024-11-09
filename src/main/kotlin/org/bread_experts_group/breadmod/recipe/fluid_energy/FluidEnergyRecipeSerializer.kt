package org.bread_experts_group.breadmod.recipe.fluid_energy

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
import net.neoforged.neoforge.fluids.crafting.FluidIngredient

class FluidEnergyRecipeSerializer : RecipeSerializer<FluidEnergyRecipe> {

    // ShapelessRecipe.java
    companion object {
        val CODEC: MapCodec<FluidEnergyRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                Ingredient.CODEC
                    .listOf()
                    .fieldOf("item_ingredients")
                    .flatXmap(
                        { list ->
                            val typedArray = list.toTypedArray()
                            DataResult.success(NonNullList.of(Ingredient.EMPTY, *typedArray))
                        }, { result -> DataResult.success(result) }
                    ).forGetter(FluidEnergyRecipe::itemIngredients),
                FluidIngredient.CODEC
                    .listOf()
                    .fieldOf("fluid_ingredients")
                    .flatXmap(
                        { fluidList ->
                            val fluidArray = fluidList.toTypedArray()
                            DataResult.success(NonNullList.of(FluidIngredient.of(), *fluidArray))
                        }, { result -> DataResult.success(result) }
                    ).forGetter(FluidEnergyRecipe::fluidIngredients),
                ItemStack.CODEC.listOf().fieldOf("result").forGetter(FluidEnergyRecipe::results),
                Codec.INT.fieldOf("energy").forGetter(FluidEnergyRecipe::energy),
                Codec.INT.fieldOf("time").forGetter(FluidEnergyRecipe::time)
            ).apply(inst, ::FluidEnergyRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity(cap) }),
            FluidEnergyRecipe::itemIngredients,
            FluidIngredient.STREAM_CODEC.apply(ByteBufCodecs.collection { cap -> NonNullList.createWithCapacity(cap) }),
            FluidEnergyRecipe::fluidIngredients,
            ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::results,
            ByteBufCodecs.INT, FluidEnergyRecipe::energy,
            ByteBufCodecs.INT, FluidEnergyRecipe::time,
            ::FluidEnergyRecipe
        )

//        val STREAM_CODEC = StreamCodec.of(::toNetwork, ::fromNetwork)

/*        fun fromNetwork(buffer: RegistryFriendlyByteBuf): FluidEnergyRecipe {
            val i = buffer.readVarInt()
            val ingredientList: NonNullList<Ingredient> = NonNullList.withSize(i, Ingredient.EMPTY)
            ingredientList.replaceAll { _ -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer) }

            val f = buffer.readVarInt()
            val fluidIngredientList = NonNullList.withSize(f, FluidIngredient.of())
            fluidIngredientList.replaceAll { _ -> FluidIngredient.STREAM_CODEC.decode(buffer) }

            val results = ItemStack.LIST_STREAM_CODEC.decode(buffer)
            val energy = buffer.readInt()
            val time = buffer.readInt()

            return FluidEnergyRecipe(ingredientList, fluidIngredientList, results, energy, time)
        }

        fun toNetwork(buffer: RegistryFriendlyByteBuf, recipe: FluidEnergyRecipe) {
            buffer.writeVarInt(recipe.itemIngredients.size)
            for (ingredient: Ingredient in recipe.itemIngredients) {
                Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient)
            }

            buffer.writeVarInt(recipe.fluidIngredients.size)
            for (fluidIngredient: FluidIngredient in recipe.fluidIngredients) {
                FluidIngredient.STREAM_CODEC.encode(buffer, fluidIngredient)
            }

            ItemStack.LIST_STREAM_CODEC.encode(buffer, recipe.results)
            buffer.writeInt(recipe.energy)
            buffer.writeInt(recipe.time)
        }*/
    }

    override fun codec(): MapCodec<FluidEnergyRecipe> = CODEC

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = STREAM_CODEC
}