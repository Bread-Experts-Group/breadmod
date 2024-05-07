package breadmod.block

import breadmod.registry.fluid.ModFluids
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.Item
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.material.FluidState
import net.minecraftforge.fluids.FluidType
import java.util.*

abstract class BreadLiquidBlock: FlowingFluid() {
    override fun getExplosionResistance(): Float = 100.0F

    @Deprecated("Deprecated in Java", ReplaceWith("false"))
    override fun canConvertToSource(pLevel: Level): Boolean = false

    override fun canBeReplacedWith(
        pState: FluidState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pFluid: Fluid,
        pDirection: Direction,
    ): Boolean = false

    override fun createLegacyBlock(pState: FluidState): BlockState = ModFluids.BREAD_LIQUID.block.get().defaultBlockState()
        .setValue(LiquidBlock.LEVEL, Integer.valueOf(getLegacyLevel(pState)))

    override fun beforeDestroyingBlock(pLevel: LevelAccessor, pPos: BlockPos, pState: BlockState) {
        val blockEntity = if (pState.hasBlockEntity()) pLevel.getBlockEntity(pPos) else null
        Block.dropResources(pState, pLevel, pPos, blockEntity)
    }

    override fun getPickupSound(): Optional<SoundEvent> = Optional.of(SoundEvents.BUCKET_FILL)

    override fun getTickDelay(pLevel: LevelReader): Int = 10
    override fun getSlopeFindDistance(pLevel: LevelReader): Int = 4
    override fun getDropOff(pLevel: LevelReader): Int = 1

    override fun getBucket(): Item = ModFluids.BREAD_LIQUID.bucket.get()

    override fun getFlowing(): Fluid = ModFluids.BREAD_LIQUID.flowing.get()
    class Flowing: BreadLiquidBlock() {
        override fun createFluidStateDefinition(pBuilder: StateDefinition.Builder<Fluid, FluidState>) {
            pBuilder.add(LEVEL)
            super.createFluidStateDefinition(pBuilder)
        }

        override fun isSource(pState: FluidState): Boolean = false
        override fun getAmount(pState: FluidState): Int = pState.getValue(LEVEL)
    }

    override fun getSource(): Fluid = ModFluids.BREAD_LIQUID.source.get()
    class Source: BreadLiquidBlock() {
        override fun isSource(pState: FluidState): Boolean = true
        override fun getAmount(pState: FluidState): Int = 8
    }

    override fun getFluidType(): FluidType = ModFluids.BREAD_LIQUID.type
}