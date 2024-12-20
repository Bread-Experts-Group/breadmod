package org.bread_experts_group.breadmod.registry.recipe.actual.dough_machine

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.fluids.FluidStack

class DoughMachineRecipeSerializer : RecipeSerializer<DoughMachineRecipe> {
	private val codec : MapCodec<DoughMachineRecipe> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			ItemStack.CODEC.fieldOf("ingredient").forGetter(DoughMachineRecipe::recipeInput),
			ItemStack.CODEC.fieldOf("result").forGetter(DoughMachineRecipe::recipeOutput),
			FluidStack.CODEC.fieldOf("fluid_ingredient").forGetter(DoughMachineRecipe::recipeFluidInput),
			FluidStack.CODEC.fieldOf("fluid_result").forGetter(DoughMachineRecipe::recipeFluidOutput),
			Codec.INT.fieldOf("energy").forGetter(DoughMachineRecipe::recipeEnergy),
			Codec.INT.fieldOf("time").forGetter(DoughMachineRecipe::recipeTime)
		).apply(inst, ::DoughMachineRecipe)
	}
	private val streamCodec : StreamCodec<RegistryFriendlyByteBuf, DoughMachineRecipe> = StreamCodec.composite(
		ItemStack.STREAM_CODEC, DoughMachineRecipe::recipeInput,
		ItemStack.STREAM_CODEC, DoughMachineRecipe::recipeOutput,
		FluidStack.STREAM_CODEC, DoughMachineRecipe::recipeFluidInput,
		FluidStack.STREAM_CODEC, DoughMachineRecipe::recipeFluidOutput,
		ByteBufCodecs.INT, DoughMachineRecipe::recipeEnergy,
		ByteBufCodecs.INT, DoughMachineRecipe::recipeTime,
		::DoughMachineRecipe
	)

	override fun codec() : MapCodec<DoughMachineRecipe> = this.codec
	override fun streamCodec() : StreamCodec<RegistryFriendlyByteBuf, DoughMachineRecipe> = this.streamCodec
}