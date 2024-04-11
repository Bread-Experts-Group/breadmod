package breadmod.block

import breadmod.mixin.SpecialFireAction
import breadmod.registry.block.ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState

@SpecialFireAction
class BreadBlock : FlammableBlock(Properties.copy(Blocks.HAY_BLOCK).strength(0.5f)) {
    override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int
            = 120

    override fun onCaughtFire(
        state: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        direction: Direction?,
        igniter: LivingEntity?
    ) {
        pLevel.setBlockAndUpdate(pPos, LOW_DENSITY_CHARCOAL_BLOCK.get().block.defaultBlockState())
    }
}