package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileWeaponItem
import org.bread_experts_group.breadmod.registry.item.ModItems
import java.util.function.Predicate

class BreadGunItem : ProjectileWeaponItem(Properties().stacksTo(1).durability(9000)) {
	@Deprecated("Deprecated in Java")
	override fun getAllSupportedProjectiles() : Predicate<ItemStack> =
		Predicate { stack -> stack.`is`(ModItems.BREAD_BULLET.asItem()) }

	override fun getDefaultProjectileRange() : Int = 50
	override fun shootProjectile(
		shooter : LivingEntity,
		projectile : Projectile,
		index : Int,
		velocity : Float,
		inaccuracy : Float,
		angle : Float,
		target : LivingEntity?
	) {
		TODO("Not yet implemented")
	}
}