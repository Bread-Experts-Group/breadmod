package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class SoundBlockEntity(
	pos : BlockPos,
	state : BlockState
) : BlockEntity(ModBlockEntityTypes.SOUND_BLOCK.get(), pos, state), Container {
	var items : NonNullList<ItemStack> = NonNullList.withSize(8, ItemStack.EMPTY)
	override fun clearContent() : Unit = this.items.clear()
	override fun getContainerSize() : Int = this.items.size
	override fun isEmpty() : Boolean = this.items.isEmpty()
	override fun getItem(slot : Int) : ItemStack = this.items[slot]
	override fun removeItem(slot : Int, amount : Int) : ItemStack =
		this.items[slot].split(amount) ?: ItemStack.EMPTY

	override fun removeItemNoUpdate(slot : Int) : ItemStack =
		this.items[slot].copyAndClear() ?: ItemStack.EMPTY

	override fun setItem(slot : Int, stack : ItemStack) {
		this.items[slot] = stack
		this.setChanged()
	}

	override fun stillValid(player : Player) : Boolean = true
}