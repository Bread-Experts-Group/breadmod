package org.bread_experts_group.breadmod.registry.recipe.actual.crafting

import net.minecraft.core.HolderLookup
import net.minecraft.core.NonNullList
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.CraftingBookCategory
import net.minecraft.world.item.crafting.CraftingInput
import net.minecraft.world.item.crafting.CustomRecipe
import net.minecraft.world.level.Level
import kotlin.math.min

abstract class AbstractCuttingRecipe(
    private val input: ItemStack,
    val output: ItemStack,
    private val outputMultiplier: Int,
    private val toolTag: TagKey<Item>
) : CustomRecipe(CraftingBookCategory.MISC) {
    override fun matches(input: CraftingInput, level: Level): Boolean {
        var hasItem = false
        var hasTool = false
        input.items().forEach {
            if (!it.isEmpty) {
                if (it.item == this.input.item) hasItem = true
                else if (it.`is`(toolTag) && !hasTool) hasTool = true
                else return false
            }
        }
        return hasTool && hasItem
    }

    override fun assemble(input: CraftingInput, registries: HolderLookup.Provider): ItemStack {
        var toolStack = ItemStack.EMPTY

        var valid = true
        val itemToSplit = mutableListOf<ItemStack>()
        for (slot in 0 until input.size()) {
            var shouldBreak = false
            input.getItem(slot).also { stack ->
                if (!stack.isEmpty)
                    if (stack.`is`(this.input.item))
                        if (((itemToSplit.size + 1) * outputMultiplier) <= this.output.maxStackSize) itemToSplit.add(
                            stack
                        )
                        else shouldBreak = true
                    else if (stack.`is`(toolTag)) {
                        if (toolStack.isEmpty) toolStack = stack
                        else valid = false
                    }
            }
            if (shouldBreak || !valid) break
        }

        return if (toolStack.isEmpty || !valid) toolStack
        else ItemStack(
            this.output.item,
            min(
                itemToSplit.size,
                toolStack.item.getMaxDamage(toolStack) - toolStack.item.getDamage(toolStack)
            ) * outputMultiplier
        )
    }

    override fun getRemainingItems(input: CraftingInput): NonNullList<ItemStack> =
        NonNullList.withSize(input.size(), ItemStack.EMPTY).also {
            var count = 0
            var tool = ItemStack.EMPTY
            for (slot in 0 until it.size) {
                val stack = input.getItem(slot).copy()
                if (stack.`is`(toolTag)) {
                    it[slot] = stack
                    tool = stack
                } else if (stack.`is`(this.input.item)) count += 1
            }
            if (tool.damageValue >= tool.maxDamage) {
                tool.shrink(1)
                tool.damageValue = 0
            }
        }

    override fun canCraftInDimensions(width: Int, height: Int): Boolean = (width * height) >= 2
}