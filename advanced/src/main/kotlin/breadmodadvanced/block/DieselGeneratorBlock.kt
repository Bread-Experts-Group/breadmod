package breadmodadvanced.block

import breadmod.block.machine.BaseAbstractMachineBlock
import breadmodadvanced.block.entity.DieselGeneratorBlockEntity
import breadmodadvanced.registry.block.ModBlockEntitiesAdv
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class DieselGeneratorBlock: BaseAbstractMachineBlock.Powered<DieselGeneratorBlockEntity>(
    ModBlockEntitiesAdv.DIESEL_GENERATOR,
    Properties.of().noOcclusion()
        .strength(2f, 6.0f)
        .sound(SoundType.METAL),
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<DieselGeneratorBlockEntity> =
        BlockEntityTicker { tLevel, pPos, tState, pBlockEntity -> pBlockEntity.tick(tLevel, pPos, tState, pBlockEntity) }
}