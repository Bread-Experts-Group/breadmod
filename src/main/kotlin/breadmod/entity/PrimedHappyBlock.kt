package breadmod.entity

import breadmod.registry.ModConfiguration
import breadmod.registry.entity.ModEntityTypes
import breadmod.util.level.BMExplosion
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

class PrimedHappyBlock(
    pLevel: Level,
    pPos: Vec3 = Vec3.ZERO,
    pDelta: Vec3 = Vec3.ZERO,
    private val owner: Entity? = null,
    private var shouldSpread: Boolean = false
) : PrimedTnt(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), pLevel) {
    init {
        this.setPos(pPos); this.deltaMovement = pDelta
    }

    private val spreadRadius = ModConfiguration.COMMON.HAPPY_BLOCK_SPREAD_RADIUS.get()
    private val divisions = ModConfiguration.COMMON.HAPPY_BLOCK_DIVISIONS.get()

    override fun explode() = level().let {
        BMExplosion(it, owner, position(), 10.0, 5, Explosion.BlockInteraction.DESTROY).explodeThreaded()
        if (shouldSpread) {
            repeat(divisions) { arc ->
                val current = arc.toDouble()
                val extraPrimedHappyBlock = PrimedHappyBlock(
                    it, position(),
                    Vec3(spreadRadius * cos(current), 0.5, spreadRadius * sin(current)),
                    this.owner
                )
                it.addFreshEntity(extraPrimedHappyBlock)
            }
        }
    }

    override fun save(pCompound: CompoundTag): Boolean =
        if (!pCompound.getBoolean("shouldSpread")) {
            pCompound.putBoolean("shouldSpread", true)
            true
        } else false

    override fun load(pCompound: CompoundTag) {
        shouldSpread = pCompound.getBoolean("shouldSpread")
        super.load(pCompound)
    }
}
