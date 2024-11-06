package org.bread_experts_group.breadmod.recipe.fluid_energy

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.NonNullList
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.tags.TagKey

class FluidEnergyRecipeSerializer : RecipeSerializer<FluidEnergyRecipe> {
    companion object {
        val CODEC: MapCodec<FluidEnergyRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                ItemStack.CODEC.listOf().fieldOf("ingredients").fieldOf("item")
                    .forGetter(FluidEnergyRecipe::recipeInput),
                TagKey.codec(Registries.ITEM).listOf().fieldOf("ingredients").fieldOf("tag")
                    .forGetter(FluidEnergyRecipe::recipeInputTagged),
                ItemStack.CODEC.listOf().fieldOf("output").forGetter(FluidEnergyRecipe::recipeOutput)
            ).apply(inst, ::FluidEnergyRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC,
            FluidEnergyRecipe::recipeInput,
            ByteBufCodecs.fromCodec(TagKey.codec(Registries.ITEM))
                .apply(ByteBufCodecs.collection { capacity -> NonNullList.createWithCapacity(capacity) }),
            FluidEnergyRecipe::recipeInputTagged,
            ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::recipeOutput,
            ::FluidEnergyRecipe
        )

        /*
                val CODEC: MapCodec<FluidEnergyRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                ItemStack.CODEC.listOf().fieldOf("ingredient").forGetter(FluidEnergyRecipe::recipeInput),
                Codec.pair(TagKey.codec(Registries.ITEM), Codec.INT).listOf().fieldOf("ingredient")
                    .forGetter(FluidEnergyRecipe::recipeInputTagged),
                ItemStack.CODEC.listOf().fieldOf("output").forGetter(FluidEnergyRecipe::recipeOutput)
            ).apply(inst, ::FluidEnergyRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = StreamCodec.composite(
            ItemStack.LIST_STREAM_CODEC,
            FluidEnergyRecipe::recipeInput,
            StreamCodec.composite(
                ByteBufCodecs.fromCodec(TagKey.codec(Registries.ITEM)), Pair<TagKey<Item>, Int>::getFirst,
                ByteBufCodecs.INT, Pair<TagKey<Item>, Int>::getSecond,
                ::Pair
            ).apply(ByteBufCodecs.collection { capacity -> NonNullList.createWithCapacity(capacity) }),
            FluidEnergyRecipe::recipeInputTagged,
            ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::recipeOutput,
            ::FluidEnergyRecipe
        )
         */
    }

    override fun codec(): MapCodec<FluidEnergyRecipe> = CODEC

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> = STREAM_CODEC
}