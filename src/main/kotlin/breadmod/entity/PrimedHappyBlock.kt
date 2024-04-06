package breadmod.entity

import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Level
import kotlin.math.cos
import kotlin.math.sin

class PrimedHappyBlock(pEntityType: EntityType<PrimedHappyBlock>, pLevel: Level) : PrimedTnt(pEntityType, pLevel) {
    private var owner: LivingEntity? = null

    init {
        this.blocksBuilding = true
    }

    constructor(pLevel: Level, pX: Double, pY: Double, pZ: Double, pOwner: LivingEntity?) : this(
        ModEntities.HAPPY_BLOCK_ENTITY.get(),
        pLevel
    ) {
        this.setPos(pX, pY, pZ)
        val d0 = pLevel.random.nextDouble() * (Math.PI.toFloat() * 2f).toDouble()
        this.setDeltaMovement(-sin(d0) * 0.02, 0.2, -cos(d0) * 0.02)
        this.fuse = 80
        this.xo = pX
        this.yo = pY
        this.zo = pZ
        this.owner = pOwner
    }

    override fun explode() {
        val blastRadius = 50.0f
        level().explode(this, this.x, this.getY(0.0625), this.z, blastRadius, Level.ExplosionInteraction.TNT)
    }
}
