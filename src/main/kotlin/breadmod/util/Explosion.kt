package breadmod.util

import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.mojang.datafixers.util.Pair
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.ProtectionEnchantment
import net.minecraft.world.level.*
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.storage.loot.LootParams
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import net.minecraftforge.event.ForgeEventFactory
import java.util.function.Consumer
import kotlin.math.floor
import kotlin.math.sqrt

class Explosion(
    private val level: Level,
    /**
     * Returns either the entity that placed the explosive block, the entity that caused the explosion or null.
     */
    val exploder: Entity?,
    pDamageSource: DamageSource?,
    pDamageCalculator: ExplosionDamageCalculator?,
    private val x: Double,
    private val y: Double,
    private val z: Double,
    private val radius: Float,
    private val fire: Boolean,
    private val blockInteraction: Explosion.BlockInteraction
) {
    private val random: RandomSource = RandomSource.create()
    val damageSource: DamageSource = pDamageSource ?: level.damageSources().explosion(this)
    private val damageCalculator: ExplosionDamageCalculator
    private val toBlow = ObjectArrayList<BlockPos>()
    private val hitPlayers: MutableMap<Player, Vec3> = Maps.newHashMap()
    val position: Vec3

    constructor(
        pLevel: Level,
        pSource: Entity?,
        pToBlowX: Double,
        pToBlowY: Double,
        pToBlowZ: Double,
        pRadius: Float,
        pPositions: List<BlockPos>?
    ) : this(
        pLevel,
        pSource,
        pToBlowX,
        pToBlowY,
        pToBlowZ,
        pRadius,
        false,
        Explosion.BlockInteraction.DESTROY_WITH_DECAY,
        pPositions
    )

    constructor(
        pLevel: Level,
        pSource: Entity?,
        pToBlowX: Double,
        pToBlowY: Double,
        pToBlowZ: Double,
        pRadius: Float,
        pFire: Boolean,
        pBlockInteraction: Explosion.BlockInteraction,
        pPositions: List<BlockPos>?
    ) : this(pLevel, pSource, pToBlowX, pToBlowY, pToBlowZ, pRadius, pFire, pBlockInteraction) {
        toBlow.addAll(pPositions!!)
    }

    constructor(
        pLevel: Level,
        pSource: Entity?,
        pToBlowX: Double,
        pToBlowY: Double,
        pToBlowZ: Double,
        pRadius: Float,
        pFire: Boolean,
        pBlockInteraction: Explosion.BlockInteraction
    ) : this(
        pLevel,
        pSource,
        null as DamageSource?,
        null as ExplosionDamageCalculator?,
        pToBlowX,
        pToBlowY,
        pToBlowZ,
        pRadius,
        pFire,
        pBlockInteraction
    )

    init {
        this.damageCalculator = pDamageCalculator ?: this.makeDamageCalculator(
            exploder
        )
        this.position = Vec3(this.x, this.y, this.z)
    }

    private fun makeDamageCalculator(pEntity: Entity?): ExplosionDamageCalculator {
        return (if (pEntity == null) EXPLOSION_DAMAGE_CALCULATOR else EntityBasedExplosionDamageCalculator(pEntity))
    }

    /**
     * Does the first part of the explosion (destroy blocks)
     */
    fun explode() {
        level.gameEvent(this.exploder, GameEvent.EXPLODE, Vec3(this.x, this.y, this.z))
        val set: MutableSet<BlockPos> = Sets.newHashSet()
        val i = 16

        for (j in 0..15) {
            for (k in 0..15) {
                for (l in 0..15) {
                    if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                        var d0 = (j.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                        var d1 = (k.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                        var d2 = (l.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                        val d3 = sqrt(d0 * d0 + d1 * d1 + d2 * d2)
                        d0 /= d3
                        d1 /= d3
                        d2 /= d3
                        var f = this.radius * (0.7f + level.random.nextFloat() * 0.6f)
                        var d4 = this.x
                        var d6 = this.y
                        var d8 = this.z

                        val f1 = 0.3f
                        while (f > 0.0f) {
                            val blockpos = BlockPos.containing(d4, d6, d8)
                            val blockstate = level.getBlockState(blockpos)
                            val fluidstate = level.getFluidState(blockpos)
                            if (!level.isInWorldBounds(blockpos)) {
                                break
                            }

                            val optional = damageCalculator.getBlockExplosionResistance(
                                this,
                                this.level,
                                blockpos,
                                blockstate,
                                fluidstate
                            )
                            if (optional.isPresent) {
                                f -= (optional.get() + 0.3f) * 0.3f
                            }

                            if (f > 0.0f && damageCalculator.shouldBlockExplode(
                                    this,
                                    this.level,
                                    blockpos,
                                    blockstate,
                                    f
                                )
                            ) {
                                set.add(blockpos)
                            }

                            d4 += d0 * 0.3
                            d6 += d1 * 0.3
                            d8 += d2 * 0.3
                            f -= 0.22500001f
                        }
                    }
                }
            }
        }

        toBlow.addAll(set)
        val f2 = this.radius * 2.0f
        val k1 = Mth.floor(this.x - (f2.toDouble()) - 1.0)
        val l1 = Mth.floor(this.x + (f2.toDouble()) + 1.0)
        val i2 = Mth.floor(this.y - (f2.toDouble()) - 1.0)
        val i1 = Mth.floor(this.y + (f2.toDouble()) + 1.0)
        val j2 = Mth.floor(this.z - (f2.toDouble()) - 1.0)
        val j1 = Mth.floor(this.z + (f2.toDouble()) + 1.0)
        val list = level.getEntities(
            this.exploder,
            AABB(k1.toDouble(), i2.toDouble(), j2.toDouble(), l1.toDouble(), i1.toDouble(), j1.toDouble())
        )
        ForgeEventFactory.onExplosionDetonate(this.level, this, list, f2.toDouble())
        val vec3 = Vec3(this.x, this.y, this.z)

        for (k2 in list.indices) {
            val entity = list[k2]
            if (!entity.ignoreExplosion()) {
                val d12 = sqrt(entity.distanceToSqr(vec3)) / f2.toDouble()
                if (d12 <= 1.0) {
                    var d5 = entity.x - this.x
                    var d7 = (if (entity is PrimedTnt) entity.getY() else entity.eyeY) - this.y
                    var d9 = entity.z - this.z
                    val d13 = sqrt(d5 * d5 + d7 * d7 + d9 * d9)
                    if (d13 != 0.0) {
                        d5 /= d13
                        d7 /= d13
                        d9 /= d13
                        val d14 = getSeenPercent(vec3, entity).toDouble()
                        val d10 = (1.0 - d12) * d14
                        entity.hurt(
                            this.damageSource,
                            ((d10 * d10 + d10) / 2.0 * 7.0 * (f2.toDouble()) + 1.0).toInt().toFloat()
                        )
                        var d11: Double
                        if (entity is LivingEntity) {
                            d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener(entity, d10)
                        } else {
                            d11 = d10
                        }

                        d5 *= d11
                        d7 *= d11
                        d9 *= d11
                        val vec31 = Vec3(d5, d7, d9)
                        entity.deltaMovement = entity.deltaMovement.add(vec31)
                        if (entity is Player) {
                            val player = entity
                            if (!player.isSpectator && (!player.isCreative || !player.abilities.flying)) {
                                hitPlayers[player] = vec31
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    fun finalizeExplosion(pSpawnParticles: Boolean) {
        if (level.isClientSide) {
            level.playLocalSound(
                this.x,
                this.y,
                this.z,
                SoundEvents.GENERIC_EXPLODE,
                SoundSource.BLOCKS,
                4.0f,
                (1.0f + (level.random.nextFloat() - level.random.nextFloat()) * 0.2f) * 0.7f,
                false
            )
        }

        val flag = this.interactsWithBlocks()
        if (pSpawnParticles) {
            if (!(this.radius < 2.0f) && flag) {
                level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0, 0.0, 0.0)
            } else {
                level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0, 0.0, 0.0)
            }
        }

        if (flag) {
            val objectarraylist = ObjectArrayList<Pair<ItemStack, BlockPos>>()
            val flag1 = indirectSourceEntity is Player
            Util.shuffle(this.toBlow, level.random)

            for (blockpos in this.toBlow) {
                val blockstate = level.getBlockState(blockpos)
                val block = blockstate.block
                if (!blockstate.isAir) {
                    val blockpos1 = blockpos.immutable()
                    level.profiler.push("explosion_blocks")
                    if (blockstate.canDropFromExplosion(this.level, blockpos, this)) {
                        val `$$9` = this.level
                        if (`$$9` is ServerLevel) {
                            val serverlevel = `$$9`
                            val blockentity = if (blockstate.hasBlockEntity()) level.getBlockEntity(blockpos) else null
                            val `lootparams$builder` =
                                LootParams.Builder(serverlevel)
                                    .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos))
                                    .withParameter(LootContextParams.TOOL, ItemStack.EMPTY)
                                    .withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockentity)
                                    .withOptionalParameter(LootContextParams.THIS_ENTITY, this.exploder)
                            if (this.blockInteraction == Explosion.BlockInteraction.DESTROY_WITH_DECAY) {
                                `lootparams$builder`.withParameter(LootContextParams.EXPLOSION_RADIUS, this.radius)
                            }

                            blockstate.spawnAfterBreak(serverlevel, blockpos, ItemStack.EMPTY, flag1)
                            blockstate.getDrops(`lootparams$builder`).forEach(Consumer { p_46074_: ItemStack ->
                                addBlockDrops(objectarraylist, p_46074_, blockpos1)
                            })
                        }
                    }

                    blockstate.onBlockExploded(this.level, blockpos, this)
                    level.profiler.pop()
                }
            }

            for (pair in objectarraylist) {
                Block.popResource(this.level, pair.second, pair.first)
            }
        }

        if (this.fire) {
            for (blockpos2 in this.toBlow) {
                if (random.nextInt(3) == 0 && level.getBlockState(blockpos2).isAir && level.getBlockState(blockpos2.below())
                        .isSolidRender(
                            this.level, blockpos2.below()
                        )
                ) {
                    level.setBlockAndUpdate(blockpos2, BaseFireBlock.getState(this.level, blockpos2))
                }
            }
        }
    }

    fun interactsWithBlocks(): Boolean {
        return this.blockInteraction != Explosion.BlockInteraction.KEEP
    }

    fun getHitPlayers(): Map<Player, Vec3> {
        return this.hitPlayers
    }

    val indirectSourceEntity: LivingEntity?
        get() {
            if (this.exploder == null) {
                return null
            } else {
                var entity = this.exploder
                if (entity is PrimedTnt) {
                    return entity.owner
                } else {
                    entity = this.exploder
                    if (entity is LivingEntity) {
                        return entity
                    } else {
                        entity = this.exploder
                        if (entity is Projectile) {
                            entity = entity.owner
                            if (entity is LivingEntity) {
                                return entity
                            }
                        }

                        return null
                    }
                }
            }
        }

    fun clearToBlow() {
        toBlow.clear()
    }

    fun getToBlow(): List<BlockPos> {
        return this.toBlow
    }

    enum class BlockInteraction {
        KEEP,
        DESTROY,
        DESTROY_WITH_DECAY
    }

    companion object {
        private val EXPLOSION_DAMAGE_CALCULATOR = ExplosionDamageCalculator()
        private const val MAX_DROPS_PER_COMBINED_STACK = 16
        fun getSeenPercent(pExplosionVector: Vec3?, pEntity: Entity): Float {
            val aabb = pEntity.boundingBox
            val d0 = 1.0 / ((aabb.maxX - aabb.minX) * 2.0 + 1.0)
            val d1 = 1.0 / ((aabb.maxY - aabb.minY) * 2.0 + 1.0)
            val d2 = 1.0 / ((aabb.maxZ - aabb.minZ) * 2.0 + 1.0)
            val d3 = (1.0 - floor(1.0 / d0) * d0) / 2.0
            val d4 = (1.0 - floor(1.0 / d2) * d2) / 2.0
            if (!(d0 < 0.0) && !(d1 < 0.0) && !(d2 < 0.0)) {
                var i = 0
                var j = 0

                var d5 = 0.0
                while (d5 <= 1.0) {
                    var d6 = 0.0
                    while (d6 <= 1.0) {
                        var d7 = 0.0
                        while (d7 <= 1.0) {
                            val d8 = Mth.lerp(d5, aabb.minX, aabb.maxX)
                            val d9 = Mth.lerp(d6, aabb.minY, aabb.maxY)
                            val d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ)
                            val vec3 = Vec3(d8 + d3, d9, d10 + d4)
                            if (pEntity.level().clip(
                                    ClipContext(
                                        vec3,
                                        pExplosionVector,
                                        ClipContext.Block.COLLIDER,
                                        ClipContext.Fluid.NONE,
                                        pEntity
                                    )
                                ).type == HitResult.Type.MISS
                            ) {
                                ++i
                            }

                            ++j
                            d7 += d2
                        }
                        d6 += d1
                    }
                    d5 += d0
                }

                return i.toFloat() / j.toFloat()
            } else {
                return 0.0f
            }
        }

        private fun addBlockDrops(
            pDropPositionArray: ObjectArrayList<Pair<ItemStack, BlockPos>>,
            pStack: ItemStack,
            pPos: BlockPos
        ) {
            val i = pDropPositionArray.size

            for (j in 0 until i) {
                val pair = pDropPositionArray[j]
                val itemstack = pair.first
                if (ItemEntity.areMergable(itemstack, pStack)) {
                    val itemstack1 = ItemEntity.merge(itemstack, pStack, 16)
                    pDropPositionArray[j] = Pair.of(itemstack1, pair.second)
                    if (pStack.isEmpty) {
                        return
                    }
                }
            }

            pDropPositionArray.add(Pair.of(pStack, pPos))
        }
    }
}
