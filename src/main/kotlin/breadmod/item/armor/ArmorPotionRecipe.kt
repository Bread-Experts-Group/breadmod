package breadmod.item.armor

import breadmod.recipe.CustomRecipeType
import breadmod.util.StackColor.applyColor
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import java.awt.Color

abstract class ArmorPotionRecipe(pId: ResourceLocation, pCategory: CraftingBookCategory) : CustomRecipe(pId, pCategory) {
    override fun matches(pInv: CraftingContainer, pLevel: Level): Boolean {
        var hasItem = false; var hasEffect = false
        for (slot in 0 until pInv.containerSize) {
            pInv.getItem(slot).also { stack ->
                if(!stack.isEmpty) when(stack.item) {
                    is PotionItem -> {
                        if(hasEffect) return false
                        PotionUtils.getPotion(stack).effects.also {
                            if(it.size != 1 || it.firstOrNull()?.effect?.isInstantenous == true) return false }
                        hasEffect = true
                    }
                    is BreadArmorItem -> {
                        if(hasItem ||stack.tag?.contains("Potion") == true) return false
                        hasItem = true
                    }
                    else -> return false
                }
            }
        }

        return hasItem && hasEffect
    }

    override fun assemble(pInv: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        var itemStack: ItemStack = ItemStack.EMPTY
        val potions = buildList {
            for (slot in 0 until pInv.containerSize) {
                val stack = pInv.getItem(slot)
                when(stack.item) {
                    is PotionItem -> addAll(PotionUtils.getPotion(stack).effects)
                    is BreadArmorItem -> if(itemStack.isEmpty) itemStack = stack else return ItemStack.EMPTY
                }
            }
        }

        return if(!itemStack.isEmpty && potions.isNotEmpty())
            itemStack.copy().also { stack ->
                stack.applyColor(Color(PotionUtils.getColor(potions)))
                PotionUtils.setCustomEffects(stack, potions)
            }
        else ItemStack.EMPTY
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = pWidth * pHeight >= 2
//    override fun getSerializer(): RecipeSerializer<ArmorPotionRecipe> = CustomRecipeType.ARMOR_POTIONS // TODO("To Be Fixed")
}