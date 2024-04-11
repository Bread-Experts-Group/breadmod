package breadmod.block

import breadmod.block.entity.BreadFurnaceBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FurnaceBlock
import net.minecraft.world.level.block.state.BlockState

class BreadFurnaceBlock: FurnaceBlock(Properties.copy(Blocks.FURNACE)) {
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState) = BreadFurnaceBlockEntity(pPos, pState)

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        if (pState.getValue(LIT)) {
            val d0 = pPos.x + 0.5
            val d1 = pPos.y.toDouble()
            val d2 = pPos.z + 0.5
            if (pRandom.nextDouble() < 0.1) {
                pLevel.playLocalSound(
                    d0, d1, d2,
                    SoundEvents.FURNACE_FIRE_CRACKLE,
                    SoundSource.BLOCKS,
                    1.0f,
                    1.0f,
                    false
                )
            }

            val direction = pState.getValue(FACING)
            val axis = direction.axis
            val d4 = pRandom.nextDouble() * 0.6 - 0.3
            val d5 = if (axis === Direction.Axis.X) direction.stepX.toDouble() * 0.52 else d4
            val d6 = pRandom.nextDouble() * 6.0 / 16.0
            val d7 = if (axis === Direction.Axis.Z) direction.stepZ.toDouble() * 0.52 else d4
            pLevel.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
            pLevel.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0, 0.0, 0.0)
        }
    }
}