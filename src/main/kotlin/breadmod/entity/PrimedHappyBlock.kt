package breadmod.entity

import breadmod.registry.entity.ModEntities
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

class PrimedHappyBlock(pEntityType: EntityType<PrimedHappyBlock>, private val pLevel: Level) : PrimedTnt(pEntityType, pLevel) {
    private var owner: LivingEntity? = null

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

//    private val randomNum = Random(-7689986)
//    private var whileX = 10
    override fun explode() {
        val blastRadius = 25.0f
        level().explode(this, this.getX(0.05), this.getY(0.0625), this.z, blastRadius, Level.ExplosionInteraction.TNT)
//        while(whileX > 0) { // DEAR GOD KEEP THIS COMMENTED UNTIL WE FIGURE OUT HOW TO STOP IT FROM RECURSIVELY ADDING MORE HAPPY BLOCKS
//            println(whileX)
//            println("added new happy block entity")
//            val extraPrimedHappyBlock = PrimedHappyBlock(pLevel, this.x, this.y, this.z, this.owner)
//            fun nextDouble() = randomNum.nextDouble(-0.5, 0.5)
//            extraPrimedHappyBlock.deltaMovement = Vec3(nextDouble(), 0.5, nextDouble())
//            pLevel.addFreshEntity(extraPrimedHappyBlock)
//            whileX--
//        }
    }

    override fun onRemovedFromWorld() {
        super.onRemovedFromWorld()
    }

    override fun isPushable(): Boolean = true
}
