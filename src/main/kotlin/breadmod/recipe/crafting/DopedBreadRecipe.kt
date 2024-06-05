package breadmod.recipe.crafting

import breadmod.registry.item.ModItems
import breadmod.registry.recipe.ModRecipeSerializers.DOPED_BREAD
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.PotionItem
import net.minecraft.world.item.alchemy.PotionUtils
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level

class DopedBreadRecipe(pId: ResourceLocation, pCategory: CraftingBookCategory) : CustomRecipe(pId, pCategory) {
    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        var hasPotion = false; var hasBread = false
        pContainer.items.forEach {
            if(!it.isEmpty)
                if(it.item is PotionItem) {
                    if(!hasPotion) hasPotion = true
                    else return false
                } else if(it.`is`(Items.BREAD)) hasBread = true
                else return false
        }

        return hasBread && hasPotion
    }

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        val itemStack = ItemStack(ModItems.DOPED_BREAD.get(), 0)
        var potionStack = ItemStack.EMPTY

        pContainer.items.forEach {
            if(it.`is`(Items.BREAD)) itemStack.grow(1)
            else if(it.item is PotionItem) potionStack = it
        }

        val effects = PotionUtils.getMobEffects(potionStack).map { MobEffectInstance(it.effect, it.duration / itemStack.count) }
        PotionUtils.setCustomEffects(itemStack, effects)
        return itemStack
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = (pWidth * pHeight) >= 2
    override fun getSerializer(): RecipeSerializer<*> = DOPED_BREAD.get()
}