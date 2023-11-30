package breadmod.core

import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.Supplier

object ArmorTiers {
    val BREAD_ARMOR: ArmorMaterial = ModArmorMaterial(
        "breadarmor",
        500, intArrayOf(2, 3, 4, 2),
        20,
        SoundEvents.ARMOR_EQUIP_LEATHER,
        0.0f,
        0.0f,
        Supplier<Ingredient> { Ingredient.of(Items.BREAD.defaultInstance) })
}