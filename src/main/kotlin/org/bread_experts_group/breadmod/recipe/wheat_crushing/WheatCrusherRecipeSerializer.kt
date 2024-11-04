package org.bread_experts_group.breadmod.recipe.wheat_crushing

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer

class WheatCrusherRecipeSerializer : RecipeSerializer<WheatCrusherRecipe> {
    companion object {
        val CODEC: MapCodec<WheatCrusherRecipe> = RecordCodecBuilder.mapCodec { inst ->
            inst.group(
                ItemStack.CODEC.fieldOf("ingredient").forGetter(WheatCrusherRecipe::recipeInput),
                ItemStack.CODEC.fieldOf("result").forGetter(WheatCrusherRecipe::recipeOutput),
                Codec.INT.fieldOf("energy").forGetter(WheatCrusherRecipe::recipeEnergy),
                Codec.INT.fieldOf("time").forGetter(WheatCrusherRecipe::recipeTime)
            ).apply(inst, ::WheatCrusherRecipe)
        }

        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, WheatCrusherRecipe> = StreamCodec.composite(
            ItemStack.STREAM_CODEC, WheatCrusherRecipe::recipeInput,
            ItemStack.STREAM_CODEC, WheatCrusherRecipe::recipeOutput,
            ByteBufCodecs.INT, WheatCrusherRecipe::recipeEnergy,
            ByteBufCodecs.INT, WheatCrusherRecipe::recipeTime,
            ::WheatCrusherRecipe
        )
    }

    override fun codec(): MapCodec<WheatCrusherRecipe> = CODEC
    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, WheatCrusherRecipe> = STREAM_CODEC
}