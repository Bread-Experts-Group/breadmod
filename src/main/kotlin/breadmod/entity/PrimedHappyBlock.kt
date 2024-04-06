package breadmod.entity

import net.minecraft.core.particles.ParticleTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.*
import net.minecraft.world.level.Level
import kotlin.math.cos
import kotlin.math.sin

open class PrimedHappyBlock(pEntityType: EntityType<*>?, pLevel: Level?) : Entity(pEntityType!!, pLevel!!), TraceableEntity {
    private var owner: LivingEntity? = null
    private val blastRadius: Float = 50.0f
    private val fuseTime: Int = 90

    init {
        this.blocksBuilding = true
    }

    constructor(pLevel: Level, pX: Double, pY: Double, pZ: Double, pOwner: LivingEntity?) : this(
        EntityType.TNT,
        pLevel
    ) {
        this.setPos(pX, pY, pZ)
        val d0 = pLevel.random.nextDouble() * (Math.PI.toFloat() * 2f).toDouble()
        this.setDeltaMovement(-sin(d0) * 0.02, 0.2, -cos(d0) * 0.02)
        this.fuse = fuseTime
        this.xo = pX
        this.yo = pY
        this.zo = pZ
        this.owner = pOwner
    }

    override fun defineSynchedData() {
        entityData.define(DATA_FUSE_ID, 100)
    }

    override fun getMovementEmission(): MovementEmission {
        return MovementEmission.NONE
    }

    override fun isPickable(): Boolean {
        return !this.isRemoved
    }

    override fun tick() {
        if (!this.isNoGravity) {
            this.deltaMovement = deltaMovement.add(0.0, -0.04, 0.0)
        }

        this.move(MoverType.SELF, this.deltaMovement)
        this.deltaMovement = deltaMovement.scale(0.98)
        if (this.onGround()) {
            this.deltaMovement = deltaMovement.multiply(0.7, -0.5, 0.7)
        }

        val i = this.fuse - 1
        this.fuse = i
        if (i <= 0) {
            this.discard()
            if (!level().isClientSide) {
                this.explode()
            }
        } else {
            this.updateInWaterStateAndDoFluidPushing()
            if (level().isClientSide) {
                level().addParticle(ParticleTypes.SMOKE, this.x, this.y + 0.5, this.z, 0.0, 0.0, 0.0)
            }
        }
    }

    private fun explode() {
        level().explode(this, this.x, this.getY(0.0625), this.z, blastRadius, Level.ExplosionInteraction.TNT)
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
        pCompound.putShort("Fuse", fuse.toShort())
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
        this.fuse = pCompound.getShort("Fuse").toInt()
    }

    override fun getOwner(): LivingEntity? {
        return this.owner
    }

    override fun getEyeHeight(pPose: Pose, pSize: EntityDimensions): Float {
        return 0.15f
    }

    var fuse: Int
        get() = entityData.get(DATA_FUSE_ID)
        set(pLife) {
            entityData.set(DATA_FUSE_ID, pLife)
        }

    companion object {
        private val DATA_FUSE_ID: EntityDataAccessor<Int> = SynchedEntityData.defineId(
            PrimedHappyBlock::class.java, EntityDataSerializers.INT
        )
    }
}
