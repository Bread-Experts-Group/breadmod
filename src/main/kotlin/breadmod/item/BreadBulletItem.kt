package breadmod.item

import breadmod.entity.BreadBullet
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BreadBulletItem: Item(Properties()) {
    fun createBullet(pLevel: Level, pStack: ItemStack, pShooter: LivingEntity): AbstractArrow {
        val bullet = BreadBullet(pLevel, pShooter)
        return bullet
    }
}