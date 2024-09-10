package bread.mod.breadmod.item

import bread.mod.breadmod.registry.item.ModItems
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.level.Level
import java.util.function.Predicate

class BreadGunItem : ProjectileWeaponItem(Properties().stacksTo(1).durability(9000)) {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val flag = pPlayer.abilities.instabuild
        val itemStack = pPlayer.getProjectile(pPlayer.getItemInHand(pUsedHand))
        val pStack = pPlayer.getItemInHand(pUsedHand)
        if (itemStack.isEmpty) return InteractionResultHolder.fail(pStack)

        if (pLevel is ServerLevel && (!itemStack.isEmpty || flag)) {
            if (!flag) {
                itemStack.shrink(1)
                if (itemStack.isEmpty) pPlayer.inventory.removeItem(itemStack)
            }
            pPlayer.cooldowns.addCooldown(this, 80)
        }

        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }

    // todo use shootProjectile to fire the bullet instead
//    override fun inventoryTick(pStack: ItemStack, pLevel: Level, pEntity: Entity, pSlotId: Int, pIsSelected: Boolean) {
//        if (pEntity is LivingEntity) {
//            if (fire) {
//                pLevel.playSound(null, pEntity.blockPosition(), ModSounds.MINIGUN.get(), SoundSource.PLAYERS)
//                fireTimes = 15
//                fire = false
//            }
//            if (!pLevel.isClientSide && fireTimes > 0) {
//                val bullet = ModEntityTypes.BREAD_BULLET_ENTITY.get().create(pLevel)
//                if (bullet != null) {
//                    pStack.hurtAndBreak(1, pEntity) { event -> event.broadcastBreakEvent(pEntity.usedItemHand) }
//
//                    bullet.shootFromRotation(pEntity, pEntity.xRot, pEntity.yRot, 0.0F, 1f, 0f)
//                    bullet.owner = pEntity
//                    bullet.baseDamage = 1.0
//                    bullet.knockback = 100
//                    pLevel.addFreshEntity(bullet)
//                }
//                fireTimes--
//            }
//        }
//    }

    override fun getAllSupportedProjectiles(): Predicate<ItemStack> =
        Predicate { stack -> stack.`is`(ModItems.BREAD_BULLET_ITEM.get()) }

    override fun getDefaultProjectileRange(): Int = 50

    override fun shootProjectile(
        shooter: LivingEntity,
        projectile: Projectile,
        index: Int,
        velocity: Float,
        inaccuracy: Float,
        angle: Float,
        target: LivingEntity?
    ) {
        TODO("Not yet implemented")
    }
}