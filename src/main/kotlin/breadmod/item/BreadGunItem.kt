package breadmod.item

import breadmod.registry.item.ModItems
import breadmod.registry.sound.ModSounds
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.Vanishable
import net.minecraft.world.level.Level
import java.util.function.Predicate

class BreadGunItem: ProjectileWeaponItem(Properties()), Vanishable {
    private val bulletOnly: Predicate<ItemStack> = Predicate { stack -> stack.`is`(ModItems.BREAD_BULLET_ITEM.get()) }

    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val flag = pPlayer.abilities.instabuild
        val itemstack = pPlayer.getProjectile(pPlayer.getItemInHand(pUsedHand))
        val pStack = pPlayer.getItemInHand(pUsedHand)

        if(!pLevel.isClientSide) {
            val bulletItem = itemstack.item as BreadBulletItem
            val abstractBullet = bulletItem.createArrow(pLevel, itemstack, pPlayer)
            abstractBullet.shootFromRotation(pPlayer, pPlayer.xRot, pPlayer.yRot, 0.0F, 10f, 0.5f)
//            pStack.hurtAndBreak(1, pPlayer) { event -> event.broadcastBreakEvent(pPlayer.usedItemHand) } // Item has no durability yet so this is redundant
            abstractBullet.baseDamage = 50.0
            pLevel.addFreshEntity(abstractBullet)

            pLevel.playSound(null, pPlayer.x, pPlayer.y, pPlayer.z, ModSounds.POW.get(), SoundSource.PLAYERS, 1.0f, 1.0f)
            if(!flag) {
                itemstack.shrink(1)
                if(itemstack.isEmpty) {
                    pPlayer.inventory.removeItem(itemstack)
                }
            }
        }

        pPlayer.cooldowns.addCooldown(this, 10)
        return InteractionResultHolder.consume(pPlayer.getItemInHand(pUsedHand))
    }

    override fun getUseAnimation(pStack: ItemStack): UseAnim = UseAnim.BOW

    override fun getAllSupportedProjectiles(): Predicate<ItemStack> = bulletOnly
    override fun getDefaultProjectileRange(): Int = 50
}