package breadmod.block.machine

import breadmod.block.machine.entity.GeneratorBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class GeneratorBlock: BaseAbstractMachineBlock.Powered<GeneratorBlockEntity>(
    ModBlockEntities.GENERATOR,
    Properties.of().noOcclusion()
        .strength(1.5f, 5.0f)
        .sound(SoundType.METAL)
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<GeneratorBlockEntity> =
        BlockEntityTicker { tLevel, pPos, tState, pBlockEntity -> pBlockEntity.tick(tLevel, pPos, tState, pBlockEntity) }
}