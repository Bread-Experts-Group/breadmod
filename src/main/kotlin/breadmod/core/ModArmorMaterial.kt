package breadmod.core

import breadmod.BreadMod
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.crafting.Ingredient
import java.util.function.Supplier

@Suppress("SpellCheckingInspection")
class ModArmorMaterial(
    private val name: String,
    private val durability: Int,
    private val protection: IntArray,
    private val enchantability: Int,
    private val equipSound: SoundEvent,
    private val toughness: Float,
    private val knockbackResistance: Float,
    private val repairMaterial: Supplier<Ingredient>
) :
    ArmorMaterial {
    override fun getDurabilityForSlot(slot: EquipmentSlot): Int {
        return DURABILITY_PER_SLOT[slot.index] * durability
    }

    override fun getDefenseForSlot(slot: EquipmentSlot): Int {
        return protection[slot.index]
    }

    override fun getEnchantmentValue(): Int {
        return enchantability
    }

    override fun getEquipSound(): SoundEvent {
        return equipSound
    }

    override fun getRepairIngredient(): Ingredient {
        return repairMaterial.get()
    }

    override fun getName(): String {
        return BreadMod.ID + ":" + name
    }

    override fun getToughness(): Float {
        return toughness
    }

    override fun getKnockbackResistance(): Float {
        return knockbackResistance
    }

    companion object {
        private val DURABILITY_PER_SLOT = intArrayOf(13, 15, 16, 11)
    }
}