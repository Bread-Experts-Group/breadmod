package breadmod.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface LightningStrikeAction {
    fun onLightningStruck(pLevel: Level, pPos: BlockPos, pState: BlockState)
}
