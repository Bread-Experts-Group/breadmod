package breadmod.block

import breadmod.block.entity.CoalGeneratorBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

class CoalGeneratorBlock: AbstractPowerGeneratorBlock() {
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity = CoalGeneratorBlockEntity(pPos, pState)

    override fun <T : BlockEntity> getTicker(
        pLevel: Level,
        pState: BlockState,
        pBlockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? =
        if(pLevel.isClientSide()) null
        else BlockEntityTicker<T> { _, pPos, _, pBlockEntity -> (pBlockEntity as CoalGeneratorBlockEntity).tick(pLevel, pPos, pState, pBlockEntity) }

    // todo block texture, blockstates
}