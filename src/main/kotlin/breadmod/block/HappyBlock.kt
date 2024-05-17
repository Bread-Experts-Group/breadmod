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
import kotlin.random.Random

class HappyBlock : TntBlock(Properties.copy(Blocks.TNT)) {
    private val random = Random(-7689986)
    override fun onCaughtFire(
        state: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        face: Direction?,
        pEntity: LivingEntity?
    ) {
        if (!pLevel.isClientSide) {
            val primedHappyBlock = PrimedHappyBlock(pLevel, pPos.x.toDouble() + 0.5, pPos.y.toDouble(), pPos.z.toDouble() + 0.5, pEntity)
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
            var x = 10
            while(x > 0) {
                println(x)
                println("added new happy block entity")
                val extraPrimedHappyBlock = PrimedHappyBlock(pLevel, pPos.x.toDouble(), pPos.y.toDouble(), pPos.z.toDouble(), pEntity)
                fun nextDouble() = random.nextDouble(-0.5, 0.5) //todo replace this with a predetermined circular spread pattern
                println(nextDouble())
                extraPrimedHappyBlock.deltaMovement = Vec3(nextDouble(), 0.5, nextDouble())
                pLevel.addFreshEntity(extraPrimedHappyBlock)
                x--
            }
        }
    }

    override fun wasExploded(pLevel: Level, pPos: BlockPos, pExplosion: Explosion) {
        if (!pLevel.isClientSide) {
            val primedHappyBlock = PrimedHappyBlock(
                pLevel,
                pPos.x.toDouble() + 0.5,
                pPos.y.toDouble(),
                pPos.z.toDouble() + 0.5,
                pExplosion.indirectSourceEntity
            )
            val i = primedHappyBlock.fuse
            primedHappyBlock.fuse = (pLevel.random.nextInt(i / 4) + i / 8).toShort().toInt()
            pLevel.addFreshEntity(primedHappyBlock)
        }
    }

    override fun isFlammable(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Boolean = true
    override fun getFlammability(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Int = 15
    override fun getFireSpreadSpeed(state: BlockState?, level: BlockGetter?, pos: BlockPos?, direction: Direction?): Int = 30
}
