package breadmod.block

import breadmod.ModMain.modLocation
import breadmod.registry.fluid.ModFluids
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions
import net.minecraftforge.fluids.ForgeFlowingFluid

abstract class BreadLiquidBlock: ForgeFlowingFluid(
    Properties({ ModFluids.BREAD_LIQUID.type.get() }, { ModFluids.BREAD_LIQUID.source.get() }, { ModFluids.BREAD_LIQUID.flowing.get() })
        .bucket { ModFluids.BREAD_LIQUID.bucket.get() }
        .explosionResistance(100F)
) {
    object ClientExtensions: IClientFluidTypeExtensions {
        override fun getFlowingTexture(): ResourceLocation = modLocation("block", "bread_liquid_flow")
        override fun getStillTexture(): ResourceLocation = modLocation("block", "bread_liquid_still")
    }

    class Flowing: BreadLiquidBlock() {
        override fun createFluidStateDefinition(pBuilder: StateDefinition.Builder<Fluid, FluidState>) {
            pBuilder.add(LEVEL)
            super.createFluidStateDefinition(pBuilder)
        }

        override fun isSource(pState: FluidState): Boolean = false
        override fun getAmount(pState: FluidState): Int = pState.getValue(LEVEL)
    }

    class Source: BreadLiquidBlock() {
        override fun isSource(pState: FluidState): Boolean = true
        override fun getAmount(pState: FluidState): Int = 8
    }
}