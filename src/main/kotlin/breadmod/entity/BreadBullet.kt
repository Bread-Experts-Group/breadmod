package breadmod.entity

import breadmod.registry.entity.ModEntityTypes
import breadmod.registry.item.ModItems
import net.minecraft.world.entity.projectile.AbstractArrow
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class BreadBullet(
    pLevel: Level
) : AbstractArrow(ModEntityTypes.BREAD_BULLET_ENTITY.get(), pLevel) {
    // todo stupid thing needs a constructor to let BreadBulletItem create this entity with a shooter
    // Arrow.java / ArrowItem.java
    override fun getPickupItem(): ItemStack = ModItems.BREAD_BULLET_ITEM.get().defaultInstance
}