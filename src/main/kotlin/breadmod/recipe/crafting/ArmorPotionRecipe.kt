package breadmod.recipe.crafting

import breadmod.item.armor.BreadArmorItem
import breadmod.registry.recipe.ModRecipeSerializers
import breadmod.util.render.applyColor
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import java.awt.Color

class ArmorPotionRecipe(pId: ResourceLocation, pCategory: CraftingBookCategory) : CustomRecipe(pId, pCategory) {
    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        var hasItem = false
        var hasEffect = false
        pContainer.items.forEach {
            if (!it.isEmpty) when (it.item) {
                is PotionItem -> {
                    if (hasEffect) return false
                    PotionUtils.getPotion(it).effects.also { effect ->
                        if (effect.size != 1 || effect.firstOrNull()?.effect?.isInstantenous == true) return false
                    }
                    hasEffect = true
                }

                is BreadArmorItem -> {
                    if (hasItem || PotionUtils.getCustomEffects(it).size > 0) return false
                    hasItem = true
                }

                else -> return false
            }
        }

        return hasItem && hasEffect
    }

    fun applyPotionForItem(effects: List<MobEffectInstance>, stack: ItemStack): ItemStack =
        stack.copy().also { copied ->
            copied.applyColor(Color(PotionUtils.getColor(effects)))
            PotionUtils.setCustomEffects(copied, effects)
        }

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        var itemStack: ItemStack = ItemStack.EMPTY
        val potions = buildList {
            pContainer.items.forEach {
                when (it.item) {
                    is PotionItem -> addAll(PotionUtils.getPotion(it).effects)
                    is BreadArmorItem -> if (itemStack.isEmpty) itemStack = it else return ItemStack.EMPTY
                }
            }
        }

        return if (!itemStack.isEmpty && potions.isNotEmpty()) applyPotionForItem(
            potions,
            itemStack
        ) else ItemStack.EMPTY
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = (pWidth * pHeight) >= 2
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.ARMOR_POTION.get()
}