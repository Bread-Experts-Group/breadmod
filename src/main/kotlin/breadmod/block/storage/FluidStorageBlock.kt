package breadmod.block.storage

import breadmod.block.machine.BaseAbstractMachineBlock
import breadmod.block.entity.storage.FluidStorageBlockEntity
import breadmod.block.util.ModBlockStateProperties
import breadmod.registry.block.ModBlockEntityTypes
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class FluidStorageBlock : BaseAbstractMachineBlock.Powered<FluidStorageBlockEntity>(
    ModBlockEntityTypes.FLUID_STORAGE,
    Properties.of(),
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING, ModBlockStateProperties.STORAGE_LEVEL)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(ModBlockStateProperties.STORAGE_LEVEL, 0)
//
//    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<FluidStorageBlockEntity> =
//        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}