package breadmod.entity

import breadmod.registry.entity.ModEntities
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

class PrimedHappyBlock(pEntityType: EntityType<PrimedHappyBlock>, pLevel: Level, private val shouldSpread: Boolean) : PrimedTnt(pEntityType, pLevel) {
    private var owner: LivingEntity? = null
    private val circularPattern: HashMap<Double, Double> = hashMapOf(
        0.0 to 1.0,
        1.0 to 0.0,
        -1.0 to 0.0,
        0.0 to -1.0
    )

    constructor(pLevel: Level, pX: Double, pY: Double, pZ: Double, pOwner: LivingEntity?, shouldSpread: Boolean) : this(
        ModEntities.HAPPY_BLOCK_ENTITY.get(),
        pLevel, shouldSpread
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

    private val spreadRadius = 0.5
    private val divisions = 4
    private val divisionsRad = divisions / 360

    override fun explode() = level().let {
        val blastRadius = 25.0f
        it.explode(owner, this.getX(0.05), this.getY(0.0625), this.z, blastRadius, Level.ExplosionInteraction.TNT)
        if(shouldSpread) {
//            circularPattern.forEach { (deltaX, deltaZ) ->
//                val extraPrimedHappyBlock = PrimedHappyBlock(it, this.x, this.y, this.z, this.owner, false)
//                extraPrimedHappyBlock.deltaMovement = Vec3(deltaX, 0.5, deltaZ)
//                it.addFreshEntity(extraPrimedHappyBlock)
//            }
            repeat(divisions) { div ->
                val current = div * divisionsRad.toDouble()
                val delta = Vec3(spreadRadius * cos(current), 0.5, spreadRadius * sin(current))
                println("X: ${delta.x} : Z: ${delta.z}")
                println("cos sin: ${cos(current)}, ${sin(current)}")
                println("raw current value: $current")
                val extraPrimedHappyBlock = PrimedHappyBlock(it, this.x, this.y, this.z, this.owner, false)
                extraPrimedHappyBlock.deltaMovement = delta
                it.addFreshEntity(extraPrimedHappyBlock)
            }
        }
    }
}
