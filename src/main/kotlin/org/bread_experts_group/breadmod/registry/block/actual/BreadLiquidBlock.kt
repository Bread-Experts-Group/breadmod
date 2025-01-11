package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions
import net.neoforged.neoforge.fluids.BaseFlowingFluid
import org.bread_experts_group.breadmod.registry.block.ModFluids
import org.bread_experts_group.breadmod.registry.block.actual.util.ILiquidCombustible
import java.awt.Color

abstract class BreadLiquidBlock private constructor() : BaseFlowingFluid(
	Properties(
		ModFluids.BREAD_LIQUID.type::get,
		ModFluids.BREAD_LIQUID.source::get,
		ModFluids.BREAD_LIQUID.flowing::get
	)
		.bucket(ModFluids.BREAD_LIQUID.bucket::get)
		.explosionResistance(100F)
), ILiquidCombustible {
	object ClientExtensions : IClientFluidTypeExtensions {
		override fun getFlowingTexture(): ResourceLocation = ResourceLocation.withDefaultNamespace("block/water_flow")
		override fun getStillTexture(): ResourceLocation = ResourceLocation.withDefaultNamespace("block/water_still")
		override fun getTintColor(): Int = Color(155, 120, 10, 200).rgb
	}

	override fun getBurnTime(): Int = 20 * 60
	class Flowing : BreadLiquidBlock() {
		override fun createFluidStateDefinition(builder: StateDefinition.Builder<Fluid, FluidState>) {
			builder.add(LEVEL)
			super.createFluidStateDefinition(builder)
		}

		override fun isSource(state: FluidState): Boolean = false
		override fun getAmount(state: FluidState): Int = state.getValue(LEVEL)
	}

	class Source : BreadLiquidBlock() {
		override fun isSource(state: FluidState): Boolean = true
		override fun getAmount(state: FluidState): Int = 8
	}
}