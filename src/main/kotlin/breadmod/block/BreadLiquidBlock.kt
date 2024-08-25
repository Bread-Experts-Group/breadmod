package breadmod.block

import breadmod.block.util.ILiquidCombustible
import breadmod.registry.fluid.ModFluids
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.ForgeFlowingFluid
import java.awt.Color

abstract class BreadLiquidBlock private constructor() : ForgeFlowingFluid(
    Properties(
        { ModFluids.BREAD_LIQUID.type.get() },
        { ModFluids.BREAD_LIQUID.source.get() },
        { ModFluids.BREAD_LIQUID.flowing.get() })
        .bucket { ModFluids.BREAD_LIQUID.bucket.get() }
        .explosionResistance(100F)
), ILiquidCombustible {
    object ClientExtensions : IClientFluidTypeExtensions {
        override fun getFlowingTexture(): ResourceLocation = ResourceLocation("block/water_flow")
        override fun getStillTexture(): ResourceLocation = ResourceLocation("block/water_still")
        override fun getTintColor(): Int = Color(155, 120, 10, 200).rgb
    }

    override fun getBurnTime(): Int = 20 * 60

    class Flowing : BreadLiquidBlock() {
        override fun createFluidStateDefinition(pBuilder: StateDefinition.Builder<Fluid, FluidState>) {
            pBuilder.add(LEVEL)
            super.createFluidStateDefinition(pBuilder)
        }

        override fun isSource(pState: FluidState): Boolean = false
        override fun getAmount(pState: FluidState): Int = pState.getValue(LEVEL)
    }

    class Source : BreadLiquidBlock() {
        override fun isSource(pState: FluidState): Boolean = true
        override fun getAmount(pState: FluidState): Int = 8
    }
}