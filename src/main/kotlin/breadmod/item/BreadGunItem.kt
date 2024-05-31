package breadmod.item

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.ProjectileWeaponItem
import java.util.function.Predicate

class BreadGunItem: ProjectileWeaponItem(Properties()) {
    override fun getAllSupportedProjectiles(): Predicate<ItemStack> {
        TODO("Not yet implemented")
    }

    override fun getDefaultProjectileRange(): Int = 50
}