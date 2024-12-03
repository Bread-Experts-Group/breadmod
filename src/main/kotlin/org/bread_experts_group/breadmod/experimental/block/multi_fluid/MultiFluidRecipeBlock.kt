package org.bread_experts_group.breadmod.experimental.block.multi_fluid

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.BlockHitResult
import net.neoforged.neoforge.fluids.FluidStack
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.ModFluids

class MultiFluidRecipeBlock : BaseEntityBlock(Properties.of()) {
    companion object {
        val CODEC: MapCodec<MultiFluidRecipeBlock> = simpleCodec { MultiFluidRecipeBlock() }
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos) as? MultiFluidRecipeBlockEntity ?: return InteractionResult.FAIL
            player.openMenu(entity, pos)
        }
        val entity = level.getBlockEntity(pos) as? MultiFluidRecipeBlockEntity ?: return InteractionResult.FAIL
        entity.tank.setFluidInTank(1, FluidStack(Fluids.LAVA, 250))
        entity.tank.setFluidInTank(0, FluidStack(ModFluids.BREAD_LIQUID.flowing.get(), 1000))
        level.setBlockAndUpdate(pos, state)
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        MultiFluidRecipeBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        blockEntityType,
        ModBlockEntityTypes.MULTI_FLUID_TEST.get()
    ) { tLevel: Level, tPos: BlockPos, tState: BlockState, tBlockEntity: MultiFluidRecipeBlockEntity ->
        tBlockEntity.tick(tLevel, tPos, tState)
    }
}