package bread.mod.breadmod.item.util

import net.minecraft.world.level.ItemLike

/**
 * Marker annotation for items that can be used as fuel in a furnace.
 * @param burnTime The number of ticks the item should burn for.
 * @throws IllegalArgumentException If the [burnTime] is less than or equal to zero, or if the annotated element
 * is not an [ItemLike].
 * @author Miko Elbrecht
 * @since 1.0.0
 */
@MustBeDocumented
@Target(AnnotationTarget.FIELD)
annotation class FuelItem(val burnTime: Int)