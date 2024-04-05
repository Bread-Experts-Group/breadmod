package breadmod.block

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState

class HappyBlock: TntBlock(Properties.copy(Blocks.TNT)) {

    override fun onDestroyedByPlayer(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        willHarvest: Boolean,
        fluid: FluidState
    ): Boolean {
        level.explode(player, pos.x.toDouble(), pos.y.toDouble(), pos.z.toDouble(), 100f, Level.ExplosionInteraction.BLOCK)
        player.kill()
//        level.explode(
//            player,
//            DamageSource(DamageTypes.EXPLOSION), // Figure out why this is throwing an error
//            ExplosionDamageCalculator(),
//            pos.x.toDouble(),
//            pos.y.toDouble(),
//            pos.z.toDouble(),
//            100f,
//            false,
//            Level.ExplosionInteraction.MOB
//        )
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid)
    }
}