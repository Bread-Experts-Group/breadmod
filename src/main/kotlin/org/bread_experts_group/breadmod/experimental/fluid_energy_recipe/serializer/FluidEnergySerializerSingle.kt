package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.serializer

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyRecipeSingle
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.RecipeFunctionDataFixerSingle
import org.bread_experts_group.breadmod.experimental.recipe.recipe.BMRecipeSerializer

class FluidEnergySerializerSingle<R : FluidEnergyRecipeSingle>(
	private val recipe : RecipeFunctionDataFixerSingle<R>
) : BMRecipeSerializer<R>() {

	override fun codec() : MapCodec<R> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			SizedIngredient.FLAT_CODEC.optionalFieldOf("item_ingredient", SizedIngredient.of(Items.AIR, 1))
				.forGetter(FluidEnergyRecipeSingle::rItemInput),
			ItemStack.CODEC.optionalFieldOf("item_result", ItemStack(Items.AIR, 1))
				.forGetter(FluidEnergyRecipeSingle::rItemOutput),
			SizedFluidIngredient.FLAT_CODEC
				.optionalFieldOf("fluid_ingredient", SizedFluidIngredient.of(Fluids.WATER, 1))
				.forGetter(FluidEnergyRecipeSingle::rFluidInput),
			FluidStack.CODEC.optionalFieldOf("fluid_result", FluidStack(Fluids.WATER, 1))
				.forGetter(FluidEnergyRecipeSingle::rFluidOutput),
			Codec.INT.optionalFieldOf("time", 0).forGetter(FluidEnergyRecipeSingle::rTime),
			Codec.INT.optionalFieldOf("energy", 0).forGetter(FluidEnergyRecipeSingle::rEnergy)
		).apply(inst, this.recipe)
	}

	override fun streamCodec() : StreamCodec<RegistryFriendlyByteBuf, R> = StreamCodec.composite(
		SizedIngredient.STREAM_CODEC, FluidEnergyRecipeSingle::rItemInput,
		ItemStack.STREAM_CODEC, FluidEnergyRecipeSingle::rItemOutput,
		SizedFluidIngredient.STREAM_CODEC, FluidEnergyRecipeSingle::rFluidInput,
		FluidStack.STREAM_CODEC, FluidEnergyRecipeSingle::rFluidOutput,
		ByteBufCodecs.INT, FluidEnergyRecipeSingle::rTime,
		ByteBufCodecs.INT, FluidEnergyRecipeSingle::rEnergy,
		this.recipe
	)
}