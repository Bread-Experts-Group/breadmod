package bread.mod.breadmod.item.tool

import bread.mod.breadmod.registry.tag.BlockTags.INCORRECT_FOR_BREAD_TOOL
import bread.mod.breadmod.registry.tag.BlockTags.INCORRECT_FOR_REINFORCED_BREAD_TOOL
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Items
import net.minecraft.world.item.Tier
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block

enum class ToolTiers(
    private val incorrectBlocksForDrops: TagKey<Block>,
    private val getUses: Int,
    private val getSpeed: Float,
    private val getAttackDamageBonus: Float,
    private val getEnchantmentValue: Int,
    private val getRepairIngredient: Ingredient
) : Tier {
    BREAD(INCORRECT_FOR_BREAD_TOOL, 100, 3f, 0.0f, 10, Ingredient.of(Items.BREAD)),
    RF_BREAD(INCORRECT_FOR_REINFORCED_BREAD_TOOL, 1500, 8f, 3f, 17, Ingredient.of(Items.NETHERITE_INGOT)); // Reinforced

    override fun getUses(): Int = getUses
    override fun getSpeed(): Float = getSpeed
    override fun getAttackDamageBonus(): Float = getAttackDamageBonus
    override fun getIncorrectBlocksForDrops(): TagKey<Block> = incorrectBlocksForDrops
    override fun getEnchantmentValue(): Int = getEnchantmentValue
    override fun getRepairIngredient(): Ingredient = getRepairIngredient
}