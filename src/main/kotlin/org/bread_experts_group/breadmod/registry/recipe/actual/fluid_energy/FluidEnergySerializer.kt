package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.crafting.RecipeSerializer
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DESCRIPTOR_FLUID_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DESCRIPTOR_FLUID_STREAM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DESCRIPTOR_ITEM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.BIG_DESCRIPTOR_ITEM_STREAM_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.U_LONG_CODEC
import org.bread_experts_group.breadmod.network.BreadModCodecs.U_LONG_STREAM_CODEC
import org.bread_experts_group.breadmod.util.listOf
import org.bread_experts_group.breadmod.util.ofOptional
import org.bread_experts_group.breadmod.util.optional
import java.util.Optional

class FluidEnergySerializer<R : FluidEnergyRecipe>(
	private val recipe: RecipeFunctionDataFixer<R>
) : RecipeSerializer<R> {
	override fun codec(): MapCodec<R> = RecordCodecBuilder.mapCodec { inst ->
		inst.group(
			Codec.optionalField("item_in", InputOption.ITEM_CODEC.listOf(), true)
				.forGetter { if (it.rItemInputs.isEmpty()) Optional.empty() else it.rItemInputs.optional() },
			Codec.optionalField("item_out", BIG_DESCRIPTOR_ITEM_CODEC.listOf(), true)
				.forGetter { if (it.rItemOutputs.isEmpty()) Optional.empty() else it.rItemOutputs.optional() },
			Codec.optionalField("fluid_in", InputOption.FLUID_CODEC.listOf(), true)
				.forGetter { if (it.rFluidInputs.isEmpty()) Optional.empty() else it.rFluidInputs.optional() },
			Codec.optionalField("fluid_out", BIG_DESCRIPTOR_FLUID_CODEC.listOf(), true)
				.forGetter { if (it.rFluidOutputs.isEmpty()) Optional.empty() else it.rFluidOutputs.optional() },
			U_LONG_CODEC.fieldOf("time").forGetter(FluidEnergyRecipe::rTime),
			Codec.optionalField("energy", BreadModCodecs.BIG_DECIMAL_CODEC, true)
				.forGetter { Optional.ofNullable(it.rEnergy) }
		).apply(inst, this.recipe)
	}

	override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, R> = StreamCodec.composite(
		InputOption.ITEM_STREAM_CODEC.listOf().ofOptional(),
		{ if (it.rItemInputs.isEmpty()) Optional.empty() else it.rItemInputs.optional() },
		BIG_DESCRIPTOR_ITEM_STREAM_CODEC.listOf().ofOptional(),
		{ if (it.rItemInputs.isEmpty()) Optional.empty() else it.rItemOutputs.optional() },
		InputOption.FLUID_STREAM_CODEC.listOf().ofOptional(),
		{ if (it.rFluidInputs.isEmpty()) Optional.empty() else it.rFluidInputs.optional() },
		BIG_DESCRIPTOR_FLUID_STREAM_CODEC.listOf().ofOptional(),
		{ if (it.rFluidInputs.isEmpty()) Optional.empty() else it.rFluidOutputs.optional() },
		U_LONG_STREAM_CODEC, FluidEnergyRecipe::rTime,
		BreadModCodecs.BIG_DECIMAL_STREAM_CODEC.ofOptional(), { Optional.ofNullable(it.rEnergy) },
		this.recipe
	)
}