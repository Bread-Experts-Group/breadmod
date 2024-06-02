package breadmod.item

import breadmod.entity.BreadBulletEntity
import breadmod.registry.item.ModItems
import breadmod.registry.sound.ModSounds
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.item.Vanishable
import net.minecraft.world.level.Level
import java.util.function.Predicate

class BreadGunItem: ProjectileWeaponItem(Properties().stacksTo(1).durability(9000)), Vanishable {
    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val flag = pPlayer.abilities.instabuild
        val itemStack = pPlayer.getProjectile(pPlayer.getItemInHand(pUsedHand))
        val pStack = pPlayer.getItemInHand(pUsedHand)
        if(itemStack.isEmpty) return InteractionResultHolder.fail(pStack)

        if(!pLevel.isClientSide && !itemStack.isEmpty || flag) {
            val entity = BreadBulletEntity(pLevel, pPlayer)
            entity.shootFromRotation(pPlayer, pPlayer.xRot, pPlayer.yRot, 0.0F, 10f, 0.5f)
            pStack.hurtAndBreak(1, pPlayer) { event -> event.broadcastBreakEvent(pPlayer.usedItemHand) } // Item has no durability yet so this is redundant
            entity.baseDamage = 1.0
            entity.knockback = 100
            pLevel.addFreshEntity(entity)

            pLevel.playSound(null, pPlayer.x, pPlayer.y, pPlayer.z, ModSounds.POW.get(), SoundSource.PLAYERS, 0.5f, 1.0f)
            if(!flag) {
                itemStack.shrink(1)
                if(itemStack.isEmpty) pPlayer.inventory.removeItem(itemStack)
                pPlayer.cooldowns.addCooldown(this, 10)
            }
        }

        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }

    override fun getAllSupportedProjectiles(): Predicate<ItemStack> = Predicate { stack -> stack.`is`(ModItems.BREAD_BULLET_ITEM.get()) }
    override fun getDefaultProjectileRange(): Int = 50
}