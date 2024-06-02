package breadmod.entity

import breadmod.registry.item.ModItems
import breadmod.registry.sound.ModSounds
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

class BreadBulletEntity: Arrow {
    // todo stupid thing needs a constructor to let BreadBulletItem create this entity with a shooter
    // todo renderer and texture
    // work on later.
    // Arrow.java / ArrowItem.java
    constructor(pLevel: Level, pShooter: LivingEntity) : super(pLevel, pShooter)
    constructor(pEntityType: EntityType<BreadBulletEntity>, pLevel: Level) : super(pEntityType, pLevel)
    // i hate this i hate this i hate this i hate this i hate this i hate this
    // WHY DOES IT WORK WHEN THE CONSTRUCTOR ISN'T IN THE CLASS DECLARATION
    // i'm going insane
    // todo GOOD GOD MAKE THIS BETTER

    override fun getPickupItem(): ItemStack = ModItems.BREAD_BULLET_ITEM.get().defaultInstance

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)

        val level = this.level()
        if(level is ServerLevel) {
            pResult.entity.playSound(ModSounds.SCREAM.get(), 100.0f, 1.0f + (random.nextFloat() * 0.25F))
            pResult.entity.changeDimension(level.server.getLevel(Level.NETHER) ?: return)
            pResult.entity.getDimensions(pResult.entity.pose).scale(2.0f)
            level.explode(this.effectSource, pResult.location.x, pResult.location.y, pResult.location.z, 100.0f, true, Level.ExplosionInteraction.MOB)
        }
    }
}