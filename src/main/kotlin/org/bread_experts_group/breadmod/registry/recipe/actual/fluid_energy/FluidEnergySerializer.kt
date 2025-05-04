package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.registry.recipe.BMRecipeSerializer
import org.bread_experts_group.breadmod.util.toMutableList
import org.bread_experts_group.breadmod.util.toNonNullList

class FluidEnergySerializer<R : FluidEnergyRecipeMulti>(
	private val recipe: RecipeFunctionDataFixer<R>
) : BMRecipeSerializer<R>() {
	override fun codec(): MapCodec<R> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			this.optionalSizedIngredientCodecModule("item_ingredients", FluidEnergyRecipeMulti::rItemInputs),
			this.optionalItemStackListCodecModule("item_results", FluidEnergyRecipeMulti::rItemOutputs),
			this.optionalSizedFluidIngredientCodecModule("fluid_ingredients", FluidEnergyRecipeMulti::rFluidInputs),
			this.optionalFluidStackListCodecModule("fluid_results", FluidEnergyRecipeMulti::rFluidOutputs),
			this.optionalIntCodecModule("time", FluidEnergyRecipeMulti::rTime),
			this.optionalIntCodecModule("energy", FluidEnergyRecipeMulti::rEnergy),
		).apply(inst, this.recipe)
	}

	override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, R> =
		StreamCodec.composite(
			SizedIngredient.STREAM_CODEC.toNonNullList(), FluidEnergyRecipeMulti::rItemInputs,
			ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipeMulti::rItemOutputs,
			SizedFluidIngredient.STREAM_CODEC.toNonNullList(), FluidEnergyRecipeMulti::rFluidInputs,
			FluidStack.STREAM_CODEC.toMutableList(), FluidEnergyRecipeMulti::rFluidOutputs,
			ByteBufCodecs.INT, FluidEnergyRecipeMulti::rTime,
			ByteBufCodecs.INT, FluidEnergyRecipeMulti::rEnergy,
			this.recipe
		)
}