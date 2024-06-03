package breadmod.entity

import breadmod.ModMain.modTranslatable
import breadmod.registry.sound.ModSounds
import net.minecraft.ChatFormatting
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.projectile.AbstractHurtingProjectile
import net.minecraft.world.level.Level
import net.minecraft.world.phys.EntityHitResult

class ToolGunShotEntity(pEntityType: EntityType<ToolGunShotEntity>, pLevel: Level): AbstractHurtingProjectile(pEntityType, pLevel) {
    // todo really scuffed projectile for vaporizing mobs, completely replace with raycasting system (when one exists)

    override fun shouldRender(pX: Double, pY: Double, pZ: Double): Boolean = false
    override fun fireImmune(): Boolean = true
    override fun isInvulnerable(): Boolean = true
    override fun isInvisible(): Boolean = false
    override fun canBeCollidedWith(): Boolean = false

    override fun onHitEntity(pResult: EntityHitResult) {
        super.onHitEntity(pResult)
        val level = this.level()
        val target = pResult.entity
        if(level is ServerLevel) {
            if(target is ServerPlayer) target.connection.disconnect(modTranslatable("item", "thestick", "playerkick"))
            else {
                target.discard()
                level.server.playerList.players.forEach { it.sendSystemMessage(
                    Component.translatable("item.breadmod.leftgame", target.name).withStyle(ChatFormatting.YELLOW)) }
            }
            target.playSound(ModSounds.TOOL_GUN.get(), 2.0f, 1f)
        } else { // todo Isn't spawning particles clientside
            fun rand() = (random.nextDouble() - 0.5)*1.2
            repeat(40) { level.addParticle(ParticleTypes.FIREWORK, target.x, target.y, target.z, rand(), random.nextDouble() + 0.1, rand()) }
        }
        this.discard()
    }

    override fun getTrailParticle(): ParticleOptions = ParticleTypes.FIREWORK

//    override fun onHitBlock(pResult: BlockHitResult) {
//        super.onHitBlock(pResult)
//        this.discard()
//    }
}