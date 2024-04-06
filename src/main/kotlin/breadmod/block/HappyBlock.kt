package breadmod.block

import breadmod.datagen.ModSounds
import breadmod.entity.PrimedHappyBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent

class HappyBlock : TntBlock(Properties.of()) {
    override fun onCaughtFire(
        state: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        face: Direction?,
        pEntity: LivingEntity?
    ) {
        if (!pLevel.isClientSide) {
            val primedHappyBlock =
                PrimedHappyBlock(pLevel, pPos.x.toDouble() + 0.5, pPos.y.toDouble(), pPos.z.toDouble() + 0.5, pEntity)
            pLevel.addFreshEntity(primedHappyBlock)
            pLevel.playSound(
                null,
                primedHappyBlock.x,
                primedHappyBlock.y,
                primedHappyBlock.z,
                ModSounds.HAPPY_BLOCK_FUSE.get(),
                SoundSource.BLOCKS,
                1.0f,
                1.0f
            )
            pLevel.gameEvent(pEntity, GameEvent.PRIME_FUSE, pPos)
        }
    }
}
