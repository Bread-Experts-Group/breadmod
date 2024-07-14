package breadmod.block.machine

import breadmod.block.machine.entity.CreativeGeneratorBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

// TODO: model for creative generator
class CreativeGeneratorBlock: BaseAbstractMachineBlock.Powered<CreativeGeneratorBlockEntity>(
    ModBlockEntities.CREATIVE_GENERATOR,
    Properties.of(),
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<CreativeGeneratorBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}