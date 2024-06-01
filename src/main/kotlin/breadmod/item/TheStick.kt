package breadmod.item

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class TheStick: Item(Properties()) {

    override fun hurtEnemy(pStack: ItemStack, pTarget: LivingEntity, pAttacker: LivingEntity): Boolean {
        pTarget.remove(Entity.RemovalReason.DISCARDED)

        return super.hurtEnemy(pStack, pTarget, pAttacker)
    }
}