package bread.mod.breadmod.entity

import bread.mod.breadmod.registry.block.ModBlocks
import bread.mod.breadmod.registry.config.CommonConfig.HAPPY_BLOCK_DIVISIONS
import bread.mod.breadmod.registry.config.CommonConfig.HAPPY_BLOCK_SPREAD_RADIUS
import bread.mod.breadmod.registry.entity.ModEntityTypes
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import kotlin.math.cos
import kotlin.math.sin

class PrimedHappyBlock(
    level: Level,
    pos: Vec3 = Vec3.ZERO,
    delta: Vec3 = Vec3.ZERO,
    private val owner: Entity? = null,
    private var shouldSpread: Boolean = false
) : PrimedTnt(ModEntityTypes.HAPPY_BLOCK_ENTITY.get(), level) {
    init {
        this.setPos(pos); this.deltaMovement = delta
    }

    private val spreadRadius = HAPPY_BLOCK_SPREAD_RADIUS.valueOrThrow()
    private val divisions = HAPPY_BLOCK_DIVISIONS.valueOrThrow()

    // todo reimplement BMExplosion
    override fun explode() = level().let {
        it.explode(
            null,
            position().x,
            position().y,
            position().z,
            30f,
            Level.ExplosionInteraction.TNT
        )
//        BMExplosion(it, owner, position(), 10.0, 5, Explosion.BlockInteraction.DESTROY).explodeThreaded()
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

    override fun getType(): EntityType<*> =
        ModEntityTypes.HAPPY_BLOCK_ENTITY.get()

    override fun getPickResult(): ItemStack = ModBlocks.HAPPY_BLOCK.get().block.asItem().defaultInstance
}