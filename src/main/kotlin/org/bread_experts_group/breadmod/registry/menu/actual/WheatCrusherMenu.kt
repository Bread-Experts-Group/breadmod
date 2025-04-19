package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class WheatCrusherMenu(
	id: Int,
	inventory: Inventory,
	parent: WheatCrusherBlockEntity
) : AbstractModContainerMenu<WheatCrusherBlockEntity>(ModMenuTypes.WHEAT_CRUSHER.get(), id, parent) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.WHEAT_CRUSHER.get()).get()
	)

	val scaledProgress: Int
		get() = ((this.parent.progress.toFloat() / this.parent.maxProgress.toFloat()) * 48).toInt()

	fun isCrafting(): Boolean = this.parent.progress > 1

	override val containerSlotCount: Int = 2

	init {
		this.addInventorySlots(inventory, 8, 174, 116)
		this.addSlot(SlotItemHandler(this.parent.itemHandler, 0, 80, 15))
		this.addSlot(ResultSlotItemHandler(this.parent.itemHandler, 1, 80, 87))
	}
}