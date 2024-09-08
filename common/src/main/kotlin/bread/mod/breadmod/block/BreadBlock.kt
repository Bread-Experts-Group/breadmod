package bread.mod.breadmod.block

import bread.mod.breadmod.block.util.ILightningStrikeAction
import net.minecraft.core.BlockPos
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class BreadBlock: Block(
    Properties
        .ofFullCopy(Blocks.HAY_BLOCK)
        .strength(0.5f, 0.5f)
        .lightLevel { state -> if (state.getValue(BlockStateProperties.POWERED)) 8 else 0 }
), ILightningStrikeAction {
    init {
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
        )
    }

    override fun onLightningStruck(pLevel: Level, pPos: BlockPos, pState: BlockState) {
        pLevel.playSound(null, pPos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 2.0F, 1.0F)
        pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, true))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWERED)
    }
}