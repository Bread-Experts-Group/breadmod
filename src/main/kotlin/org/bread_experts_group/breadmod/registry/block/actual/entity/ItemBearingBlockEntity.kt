package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Containers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

@Suppress("unused")
interface ItemBearingBlockEntity {
	val itemHandler: ExpansibleItemHandler

	fun dropContents(level: Level, position: BlockPos) {
		val list = NonNullList.createWithCapacity<ItemStack>(this.itemHandler.slots)
		repeat(this.itemHandler.slots) {
			list.add(this.itemHandler.getStackInSlot(it))
			this.itemHandler.setStackInSlot(it, ItemStack.EMPTY)
		}
		Containers.dropContents(level, position, list)
	}

	fun growItem(slot: Int, count: Int) {
		val stack = this.getItem(slot)
		stack.grow(count)
		this.itemHandler.setStackInSlot(slot, stack)
	}

	fun shrinkItem(slot: Int, count: Int) {
		val stack = this.getItem(slot)
		stack.shrink(count)
		this.itemHandler.setStackInSlot(slot, stack)
	}

	fun getItem(slot: Int): ItemStack = this.itemHandler.getStackInSlot(slot)
	fun setItem(slot: Int, stack: ItemStack): Unit = this.itemHandler.setStackInSlot(slot, stack)

	fun getItemsInRange(range: IntRange): List<ItemStack> = buildList {
		range.forEach { this.add(this@ItemBearingBlockEntity.getItem(it)) }
	}

	fun setOrGrowItem(slot: Int, stack: ItemStack, growCount: Int): Unit =
		if (this.getItem(slot).isEmpty) this.setItem(slot, stack) else this.growItem(slot, growCount)

	fun serializeItemsNBT(to: CompoundTag, registries: Provider) {
		to.put(this::class.simpleName + "_items", this.itemHandler.serializeNBT(registries))
	}

	fun deserializeItemsNBT(from: CompoundTag, registries: Provider) {
		this.itemHandler.deserializeNBT(registries, from.getCompound(this::class.simpleName + "_items"))
	}
}