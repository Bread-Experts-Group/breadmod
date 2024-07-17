package breadmod.block.machine

import breadmod.block.machine.entity.CreativeGeneratorBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class CreativeGeneratorBlock: BaseAbstractMachineBlock.Powered<CreativeGeneratorBlockEntity>(
    ModBlockEntities.CREATIVE_GENERATOR,
    Properties.of().noOcclusion(),
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    // todo toggling logic with redstone
    override fun onNeighborChange(state: BlockState, level: LevelReader, pos: BlockPos, neighbor: BlockPos) {
        super.onNeighborChange(state, level, pos, neighbor)
    }

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<CreativeGeneratorBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}