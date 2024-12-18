package org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.test

import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyRecipe
import org.bread_experts_group.breadmod.experimental.fluid_energy_recipe.FluidEnergyRecipe.FluidEnergySerializer

class FluidEnergySerializerTest : FluidEnergySerializer() {
    override fun codec(): MapCodec<FluidEnergyRecipe> =
        RecordCodecBuilder.mapCodec { inst ->
            fluidEnergyCodec(inst).apply(inst, ::FluidEnergyRecipeTest)
        }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, FluidEnergyRecipe> =
        fluidEnergyStreamCodec(::FluidEnergyRecipeTest)

}