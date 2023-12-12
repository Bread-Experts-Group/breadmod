package breadmod.item.tools

import net.minecraft.world.item.Items
import net.minecraft.world.item.Tier
import net.minecraft.world.item.crafting.Ingredient

@Suppress("MemberVisibilityCanBePrivate")
enum class ToolTiers(
    val getUses: Int,
    val getSpeed: Float,
    val getAttackDamageBonus: Float,
    val getLevel: Int,
    val getEnchantmentValue: Int,
    val getRepairIngredient: Ingredient
) : Tier {
    BREAD(50,2.5f,0.0f,0,10, Ingredient.of(Items.BREAD));
    override fun getUses(): Int = getUses
    override fun getSpeed(): Float = getSpeed
    override fun getAttackDamageBonus(): Float = getAttackDamageBonus
    @Deprecated("FORGE: Use TierSortingRegistry to define which tiers are better than others", ReplaceWith("TierSortingRegistry"))
    override fun getLevel(): Int = getLevel
    override fun getEnchantmentValue(): Int = getEnchantmentValue
    override fun getRepairIngredient(): Ingredient = getRepairIngredient
}