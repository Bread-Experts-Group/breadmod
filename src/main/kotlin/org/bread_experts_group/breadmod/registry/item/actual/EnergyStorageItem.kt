package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.ChatFormatting
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadmod.registry.component.ModDataComponents

class EnergyStorageItem(block: Block) : BlockItem(block, Properties()) {
	override fun isBarVisible(stack: ItemStack): Boolean = true

	override fun getBarColor(stack: ItemStack): Int = ChatFormatting.RED.color!!

	override fun getBarWidth(stack: ItemStack): Int = this.storedEnergyScaled(stack).toInt()

	private fun storedEnergyScaled(stack: ItemStack): Float =
		(stack.getOrDefault(ModDataComponents.ENERGY, 0).toFloat() / 1000000f * 13f)
}