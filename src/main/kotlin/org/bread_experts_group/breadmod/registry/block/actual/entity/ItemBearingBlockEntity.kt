package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.Containers
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler

@Suppress("unused")
interface ItemBearingBlockEntity : WorldlyContainer {
	val itemHandler: ExpansibleItemHandler

	fun dropContents(level: Level, position: BlockPos) {
		val list = NonNullList.createWithCapacity<ItemStack>(this.itemHandler.slots)
		repeat(this.itemHandler.slots) {
			list.add(this.itemHandler.getStackInSlot(it))
			this.itemHandler.setStackInSlot(it, ItemStack.EMPTY)
		}
		Containers.dropContents(level, position, list)
	}

	override fun getContainerSize(): Int = this.itemHandler.slots
	override fun isEmpty(): Boolean = this.itemHandler.isEmpty
	override fun removeItem(slot: Int, count: Int): ItemStack = this.itemHandler.extractItem(slot, count, false)
	override fun removeItemNoUpdate(slot: Int): ItemStack = this.itemHandler.extractItem(
		slot,
		this.itemHandler.getStackInSlot(slot).maxStackSize,
		false
	)

	override fun stillValid(player: Player): Boolean = true

	override fun getSlotsForFace(facing: Direction): IntArray = (0 .. this.itemHandler.slots).toSet().toIntArray()
	// todo these two methods aren't being respected when this handler is exposed to blocks that insert or extract items
	override fun canPlaceItemThroughFace(slot: Int, stack: ItemStack, facing: Direction?): Boolean = true
	override fun canTakeItemThroughFace(slot: Int, stack: ItemStack, facing: Direction): Boolean = true

	override fun clearContent() {
		if (!this.itemHandler.isEmpty)
			repeat(this.itemHandler.slots) { this.itemHandler.setStackInSlot(it, ItemStack.EMPTY) }
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

	override fun getItem(slot: Int): ItemStack = this.itemHandler.getStackInSlot(slot)
	override fun setItem(slot: Int, stack: ItemStack): Unit = this.itemHandler.setStackInSlot(slot, stack)

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