package breadmod.item

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.entity.projectile.Arrow
import net.minecraft.world.item.ArrowItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BreadBulletItem: ArrowItem(Properties()) {
    // todo replace with custom projectile
    // todo maybe bullets that mimic potioned arrows
    override fun createArrow(pLevel: Level, pStack: ItemStack, pShooter: LivingEntity): AbstractArrow {
        val bullet = Arrow(pLevel, pShooter)
        bullet.setEffectsFromItem(pStack)
        return bullet
    }
}