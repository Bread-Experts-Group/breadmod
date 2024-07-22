package breadmod.block.storage

import breadmod.block.ModBlockStateProperties
import breadmod.block.machine.BaseAbstractMachineBlock
import breadmod.block.storage.entity.EnergyStorageBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class EnergyStorageBlock: BaseAbstractMachineBlock.Powered<EnergyStorageBlockEntity>(
    ModBlockEntities.ENERGY_STORAGE,
    Properties.of(),
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING, ModBlockStateProperties().storageLevel)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(ModBlockStateProperties().storageLevel, 0)

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<EnergyStorageBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}