package breadmod.block.machine

import breadmod.block.machine.entity.CreativeGeneratorBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class CreativeGeneratorBlock: BaseAbstractMachineBlock.Powered<CreativeGeneratorBlockEntity>(
    ModBlockEntities.CREATIVE_GENERATOR,
    Properties.of().noOcclusion().lightLevel { 6 },
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(BlockStateProperties.ENABLED, true)

    @Deprecated("Deprecated in Java", ReplaceWith("NO"))
    override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNeighborBlock: Block,
        pNeighborPos: BlockPos,
        pMovedByPiston: Boolean
    ) {
        if(pLevel.hasNeighborSignal(pPos) && pState.getValue(BlockStateProperties.ENABLED)) {
            pLevel.playSound(null, pPos, SoundEvents.BEACON_DEACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f)
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.ENABLED, false))
        } else if(!pLevel.hasNeighborSignal(pPos) && !pState.getValue(BlockStateProperties.ENABLED)) {
            pLevel.playSound(null, pPos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0f, 1.0f)
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.ENABLED, true))
        }
    }

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<CreativeGeneratorBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}