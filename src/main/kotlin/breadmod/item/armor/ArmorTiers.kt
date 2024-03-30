package breadmod.item.armor

import breadmod.BreadMod
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.item.ArmorItem
import net.minecraft.world.item.ArmorMaterial
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient

@Suppress("SpellCheckingInspection")
// Private vals are a fix to platform clashes when kotlin implements them in getX() form.
enum class ArmorTiers(
    val durabilityMultiplier: Int,
    private val enchantmentValue    : Int,
    private val knockbackResistance : Float,
    private val toughness           : Float,
    val defense                     : List<Int>,
    private val repairIngredient    : Ingredient,
    val equipSoundEvent             : SoundEvent,
): ArmorMaterial {
    BREAD(
        durabilityMultiplier = 50, enchantmentValue = 20, knockbackResistance = 0f,     toughness = 0f,
        defense = listOf(2,3,4,2),  repairIngredient = Ingredient.of(Items.BREAD), equipSoundEvent = SoundEvents.GRASS_PLACE,
    );

    private val durabilityForSlot = listOf(13,15,16,11)

    override fun getName               (                    ): String     = "${BreadMod.ID}:${this.name.lowercase()}"
    override fun getDurabilityForType  (pType: ArmorItem.Type): Int = durabilityForSlot[pType.ordinal] * durabilityMultiplier
    override fun getDefenseForType     (pType: ArmorItem.Type): Int = defense[pType.ordinal]
    override fun getEnchantmentValue   (                    ): Int        = enchantmentValue
    override fun getRepairIngredient   (                    ): Ingredient = repairIngredient
    override fun getEquipSound         (                    ): SoundEvent = equipSoundEvent
    override fun getKnockbackResistance(                    ): Float      = knockbackResistance
    override fun getToughness          (                    ): Float      = toughness
}