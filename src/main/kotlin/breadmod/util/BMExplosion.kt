package breadmod.util

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVec3i
import java.util.function.Consumer
import kotlin.concurrent.thread

class BMExplosion(
    private val pLevel: Level,
    private val pEntity: Entity? = null,
    private val pPos: Vec3,
    private val pRadius: Double,
    private val pFire: Int?,
    private val pBlockInteraction: BlockInteraction,
): Explosion(pLevel, pEntity, pPos.x, pPos.y, pPos.z, pRadius.toFloat(), false, pBlockInteraction) {
    private val blockPos = BlockPos(pPos.toVec3i())
    private val aabb = AABB.ofSize(pPos, pRadius, pRadius, pRadius)
    private val consider = BlockPos.betweenClosedStream(aabb)
        .filter { pLevel.isInWorldBounds(it) && it.distSqr(pPos.toVec3i()) <= pRadius && !pLevel.getBlockState(it).isAir }

    override fun explode() {
        if(pLevel is ServerLevel) {
            /*pLevel.getEntities(null, aabb).forEach {
            getSeenPercent(pPos, it)
            }*/
            println("EXP")

            val toDestroy = mutableSetOf<Pair<BlockPos, BlockState>>()
            consider.forEach {
                val state = pLevel.getBlockState(it)
                DAMAGE_CALC.getBlockExplosionResistance(this, pLevel, blockPos, state, pLevel.getFluidState(it)).ifPresent { resist ->
                    if((pRadius * (0.7 + (pLevel.random.nextFloat() * 0.6)) - ((resist + 0.3) * 0.3)) > 0) toDestroy.add(it to state)
                }
                println("block explosion resistance: ${state.block}, ${state.block.getExplosionResistance(state, pLevel, blockPos, this)}")
                val float: Float = pRadius.toFloat() * (0.7F + pLevel.random.nextFloat() * 0.6F)
                if(float > 0.0F && DAMAGE_CALC.shouldBlockExplode(this, pLevel, blockPos, state, float)) toDestroy.add(it to state)
                println("block should explode: ${state.block}, $blockPos")
            }

//            val blockDrops = mutableListOf<Pair<BlockPos, ItemStack>>()
            toDestroy.forEach { (pos, state) ->
                if(!state.isAir) {
                    pLevel.profiler.push("explosion_blocks")
                    if(state.canDropFromExplosion(pLevel, pos, this)) {
                        val blockEntity = if (state.hasBlockEntity()) pLevel.getBlockEntity(pos) else null
                        val builder = LootParams.Builder(pLevel)
                            .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                            .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                            .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity)
                            .withOptionalParameter(LootContextParams.THIS_ENTITY, pEntity)
                        if (pBlockInteraction == BlockInteraction.DESTROY_WITH_DECAY)
                            builder.withParameter(LootContextParams.EXPLOSION_RADIUS, pRadius.toFloat())

                        state.spawnAfterBreak(pLevel, pos, ItemStack.EMPTY, true)
                        state.getDrops(builder).forEach(Consumer {
                            //addBlockDrops(objectarraylist, p_46074_, blockpos1)
                        })
                    }

                    //state.onBlockExploded(pLevel, blockPos, this)
                    pLevel.setBlockAndUpdate(blockPos, Blocks.AIR.defaultBlockState())
                    if(pFire != null && (pLevel.random.nextInt() * pFire == 0) && !pLevel.getBlockState(pos.below()).isSolidRender(pLevel, pos))
                        pLevel.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState())
                    pLevel.profiler.pop()
                }
            }
            println("END")
        }
    }

    fun explodeThreaded() {
        println("THR")
        if(!pLevel.isClientSide) {
            pLevel.playSound(null, BlockPos(pPos.x.toInt(), pPos.y.toInt(), pPos.z.toInt()), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f, 1.0f)
            pLevel.addParticle(
                if(!(pRadius < 2.0f) && interactsWithBlocks()) ParticleTypes.EXPLOSION_EMITTER else ParticleTypes.EXPLOSION,
                pPos.x,
                pPos.y,
                pPos.z, 1.0, 0.0, 0.0
            )
        }
//        if (pLevel.isClientSide)
//            pLevel.playLocalSound(
//                pPos.x,
//                pPos.y,
//                pPos.z,
//                SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0f,
//                (1.0f + (pLevel.random.nextFloat() - pLevel.random.nextFloat()) * 0.2f) * 0.7f, false
//            )
//        pLevel.addParticle(
//            if(!(pRadius < 2.0f) && interactsWithBlocks()) ParticleTypes.EXPLOSION_EMITTER else ParticleTypes.EXPLOSION,
//            pPos.x,
//            pPos.y,
//            pPos.z, 1.0, 0.0, 0.0
//        )
//
        thread {
            synchronized(pLevel) { explode() }
        }
    }

    companion object {
        val DAMAGE_CALC = ExplosionDamageCalculator()
    }
}