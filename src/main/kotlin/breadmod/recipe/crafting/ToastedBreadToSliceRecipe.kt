package breadmod.recipe.crafting

import breadmod.registry.item.ModItems
import breadmod.registry.recipe.ModRecipeSerializers
import net.minecraft.core.NonNullList
import net.minecraft.core.RegistryAccess
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.ItemTags
import net.minecraft.util.RandomSource
import net.minecraft.world.inventory.CraftingContainer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import kotlin.math.min

class ToastedBreadToSliceRecipe(pId: ResourceLocation, pCategory: CraftingBookCategory): CustomRecipe(pId, pCategory) {
    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        var hasToast = false; var hasSword = false
        pContainer.items.forEach {
            if(!it.isEmpty) {
                if(it.item == ModItems.TOASTED_BREAD.get()) hasToast = true
                else if(it.`is`(ItemTags.SWORDS) && !hasSword) hasSword = true
                else return false
            }
        }
        return hasToast && hasSword
    }

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        var swordItem = ItemStack.EMPTY
        val toastSliceItem = ModItems.TOAST_SLICE.get()

        var valid = true
        val toastToCut = mutableListOf<ItemStack>()
        for (slot in 0 until pContainer.containerSize) {
            var shouldBreak = false
            pContainer.getItem(slot).also { stack ->
                if(!stack.isEmpty)
                    if(stack.`is`(ModItems.TOASTED_BREAD.get()))
                        if(((toastToCut.size + 1) * 8) <= toastSliceItem.getMaxStackSize(toastSliceItem.defaultInstance)) toastToCut.add(stack)
                        //else break TODO: Get Kotlin 1.9.x
                        else shouldBreak = true
                    else if(stack.`is`(ItemTags.SWORDS)) {
                        if(swordItem.isEmpty) swordItem = stack
                        else valid = false
                    }
            }
            if(shouldBreak || !valid) break
        }

        return if(swordItem.isEmpty || swordItem.item !is SwordItem || !valid) swordItem
        else ItemStack(
            toastSliceItem,
            min(
                toastToCut.size,
                swordItem.item.getMaxDamage(swordItem) - swordItem.item.getDamage(swordItem)
            ) * 8
        )
    }

    private val toastHurtSource: RandomSource = RandomSource.create(46234821)
    override fun getRemainingItems(pContainer: CraftingContainer): NonNullList<ItemStack> = NonNullList.withSize(pContainer.containerSize, ItemStack.EMPTY).also {
        var bread = 0; var sword = ItemStack.EMPTY
        for (slot in 0 until it.size) {
            val stack = pContainer.getItem(slot).copy()
            if(stack.`is`(ItemTags.SWORDS)) {
                it[slot] = stack
                sword = stack
            } else if(stack.`is`(ModItems.TOASTED_BREAD.get())) bread += 1
        }
        if(sword.hurt(bread, toastHurtSource, null)) {
            sword.shrink(1)
            sword.damageValue = 0
        }
    }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = (pWidth * pHeight) >= 2
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.TOAST_TO_TOAST_SLICE.get()

}