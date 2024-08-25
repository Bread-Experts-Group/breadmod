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
import net.minecraft.world.item.Items
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.Level
import kotlin.math.min

class BreadSliceRecipe(pId: ResourceLocation, pCategory: CraftingBookCategory) : CustomRecipe(pId, pCategory) {
    override fun matches(pContainer: CraftingContainer, pLevel: Level): Boolean {
        var hasBread = false
        var hasSword = false
        pContainer.items.forEach {
            if (!it.isEmpty) {
                if (it.item == Items.BREAD) hasBread = true
                else if (it.`is`(ItemTags.SWORDS) && !hasSword) hasSword = true
                else return false
            }
        }
        return hasBread && hasSword
    }

    override fun assemble(pContainer: CraftingContainer, pRegistryAccess: RegistryAccess): ItemStack {
        var swordItem = ItemStack.EMPTY
        val breadSliceItem = ModItems.BREAD_SLICE.get()

        var valid = true
        val breadToCut = mutableListOf<ItemStack>()
        for (slot in 0 until pContainer.containerSize) {
            var shouldBreak = false
            pContainer.getItem(slot).also { stack ->
                if (!stack.isEmpty)
                    if (stack.`is`(Items.BREAD))
                        if (((breadToCut.size + 1) * 8) <= breadSliceItem.getMaxStackSize(breadSliceItem.defaultInstance)) breadToCut.add(
                            stack
                        )
                        //else break TODO: Get Kotlin 1.9.x
                        else shouldBreak = true
                    else if (stack.`is`(ItemTags.SWORDS)) {
                        if (swordItem.isEmpty) swordItem = stack
                        else valid = false
                    }
            }
            if (shouldBreak || !valid) break
        }

        return if (swordItem.isEmpty || swordItem.item !is SwordItem || !valid) swordItem
        else ItemStack(
            breadSliceItem,
            min(
                breadToCut.size,
                swordItem.item.getMaxDamage(swordItem) - swordItem.item.getDamage(swordItem)
            ) * 8
        )
    }

    private val breadHurtSource: RandomSource = RandomSource.create(49524592)
    override fun getRemainingItems(pContainer: CraftingContainer): NonNullList<ItemStack> =
        NonNullList.withSize(pContainer.containerSize, ItemStack.EMPTY).also {
            var bread = 0
            var sword = ItemStack.EMPTY
            for (slot in 0 until it.size) {
                val stack = pContainer.getItem(slot).copy()
                if (stack.`is`(ItemTags.SWORDS)) {
                    it[slot] = stack
                    sword = stack
                } else if (stack.`is`(Items.BREAD)) bread += 1
            }
            if (sword.hurt(bread, breadHurtSource, null)) {
                sword.shrink(1)
                sword.damageValue = 0
            }
        }

    override fun canCraftInDimensions(pWidth: Int, pHeight: Int): Boolean = (pWidth * pHeight) >= 2
    override fun getSerializer(): RecipeSerializer<*> = ModRecipeSerializers.BREAD_SLICE.get()
}