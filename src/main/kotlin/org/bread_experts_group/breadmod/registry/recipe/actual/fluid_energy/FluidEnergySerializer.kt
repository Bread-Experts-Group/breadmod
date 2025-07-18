package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeSerializer
import net.neoforged.neoforge.common.crafting.SizedIngredient
import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient
import org.bread_experts_group.breadmod.network.BreadModCodecs.optionalFluidStackListCodecModule
import org.bread_experts_group.breadmod.network.BreadModCodecs.optionalIntCodecModule
import org.bread_experts_group.breadmod.network.BreadModCodecs.optionalItemStackListCodecModule
import org.bread_experts_group.breadmod.network.BreadModCodecs.optionalSizedFluidIngredientCodecModule
import org.bread_experts_group.breadmod.network.BreadModCodecs.optionalSizedIngredientCodecModule
import org.bread_experts_group.breadmod.util.toList
import org.bread_experts_group.breadmod.util.toMutableList

class FluidEnergySerializer<R : FluidEnergyRecipe>(
	private val recipe: RecipeFunctionDataFixer<R>
) : RecipeSerializer<R> {
	override fun codec(): MapCodec<R> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			optionalSizedIngredientCodecModule("item_ingredients", FluidEnergyRecipe::rItemInputs),
			optionalItemStackListCodecModule("item_results", FluidEnergyRecipe::rItemOutputs),
			optionalSizedFluidIngredientCodecModule("fluid_ingredients", FluidEnergyRecipe::rFluidInputs),
			optionalFluidStackListCodecModule("fluid_results", FluidEnergyRecipe::rFluidOutputs),
			optionalIntCodecModule("time", FluidEnergyRecipe::rTime),
			optionalIntCodecModule("energy", FluidEnergyRecipe::rEnergy),
		).apply(inst, this.recipe)
	}

	override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, R> =
		StreamCodec.composite(
			SizedIngredient.STREAM_CODEC.toList(), FluidEnergyRecipe::rItemInputs,
			ItemStack.LIST_STREAM_CODEC, FluidEnergyRecipe::rItemOutputs,
			SizedFluidIngredient.STREAM_CODEC.toList(), FluidEnergyRecipe::rFluidInputs,
			FluidStack.STREAM_CODEC.toMutableList(), FluidEnergyRecipe::rFluidOutputs,
			ByteBufCodecs.INT, FluidEnergyRecipe::rTime,
			ByteBufCodecs.INT, FluidEnergyRecipe::rEnergy,
			this.recipe
		)
}