package breadmod.entity

import breadmod.registry.item.ModItems
import breadmod.registry.sound.ModSounds
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

class BreadBulletEntity(pEntityType: EntityType<BreadBulletEntity>, pLevel: Level) : Arrow(pEntityType, pLevel) {
    override fun getPickupItem(): ItemStack = ModItems.BREAD_BULLET_ITEM.get().defaultInstance

    override fun isInvisible(): Boolean = true
    override fun isInvulnerable(): Boolean = true
    override fun getRemainingFireTicks(): Int = 0
    override fun fireImmune(): Boolean = true
    override fun isOnFire(): Boolean = false
    override fun shouldRender(pX: Double, pY: Double, pZ: Double): Boolean = false

    override fun tick() {
        val level = this.level()
        if(level.isClientSide) level.addParticle(ParticleTypes.WHITE_ASH, x, y, z, random.nextGaussian() * 0.05, -random.nextGaussian() * 0.05, random.nextGaussian() * 0.05)
        else if(onGround()) this.discard()
        super.tick()
    }

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)

        val level = this.level()
        if(level is ServerLevel && pResult.entity != owner) {
            pResult.entity.playSound(ModSounds.SCREAM.get(), 1.0f, 1.0f + (random.nextFloat()-0.5F))
            pResult.entity.getDimensions(pResult.entity.pose).scale(2.0f)
            level.explode(this.effectSource, pResult.location.x, pResult.location.y, pResult.location.z, 5.0f, false, Level.ExplosionInteraction.NONE)
            this.discard()
        }
    }
}