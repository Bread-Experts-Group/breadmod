package breadmod.block

import breadmod.registry.sound.ModSounds
import breadmod.entity.PrimedHappyBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.TntBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.plus
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i
import kotlin.math.roundToInt
import kotlin.random.Random

class HappyBlock : TntBlock(Properties.copy(Blocks.TNT)) {
    private fun BlockPos.adjust() = this.toVec3() + Vec3(0.5, 0.0, 0.5)

    override fun onCaughtFire(
        state: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        face: Direction?,
        pEntity: LivingEntity?
    ) {
        if (!pLevel.isClientSide) {
            val primedHappyBlock = PrimedHappyBlock(pLevel, pPos.adjust(), owner = pEntity, shouldSpread = true)
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

    override fun wasExploded(pLevel: Level, pPos: BlockPos, pExplosion: Explosion) {
        if (!pLevel.isClientSide) {
            val primedHappyBlock = PrimedHappyBlock(pLevel, pPos.adjust(), owner = pExplosion.indirectSourceEntity, shouldSpread = true)
            primedHappyBlock.fuse -= ((primedHappyBlock.fuse - 10.0) * (1.0 / (1.0 + pExplosion.position.distanceTo(pPos.toVec3())))).roundToInt()
            pLevel.addFreshEntity(primedHappyBlock)
        }
    }

    override fun isFlammable(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Boolean = true
    override fun getFlammability(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Int = 15
    override fun getFireSpreadSpeed(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Int = 30
}
