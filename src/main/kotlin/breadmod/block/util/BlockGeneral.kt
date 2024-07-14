package breadmod.block.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3d
import kotlin.random.Random

fun smokeAtEdge(pLevel: Level, pPos: BlockPos, particle: ParticleOptions, soundEvent: SoundEvent?, plumes: Pair<Int, Int>, facing: Direction) {
    repeat(Random.nextInt(plumes.first, plumes.second)) {
        val normal = facing.opposite.normal
        val from = pPos.offset(normal.div(when(facing) {
            Direction.WEST, Direction.NORTH -> 1
            else -> 2
        })).toVector3d()
        val normalDouble = normal.toVector3d()
        pLevel.addParticle(
            particle,
            from.x, pPos.y + 0.25, from.z,
            (normalDouble.x + (Random.nextDouble() - 0.5)) / 20,
            0.1,
            (normalDouble.z + (Random.nextDouble() - 0.5)) / 20
        )
    }
    if (soundEvent != null && Random.nextDouble() < 0.1) {
        val posDouble = pPos.toVector3d()
        pLevel.playLocalSound(
            posDouble.x, posDouble.y, posDouble.z,
            soundEvent,
            SoundSource.BLOCKS, 1.0f, 1.0f, false
        )
    }
}