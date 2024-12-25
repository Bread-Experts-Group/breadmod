package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.energy.IEnergyStorage
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes

class WheatCrusherMenu(
	id : Int,
	inventory : Inventory,
	val parent : WheatCrusherBlockEntity
) : AbstractModContainerMenu(ModMenuTypes.WHEAT_CRUSHER.get(), id) {
	constructor(id : Int, inventory : Inventory, byteBuf : RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.WHEAT_CRUSHER.get()).get()
	)

	fun getScaledProgress() : Int = ((this.parent.progress.toFloat() / this.parent.maxProgress.toFloat()) * 48).toInt()
	fun getEnergyStoredScaled() : Int {
		return (this.parent.level ?: return 0).getCapability(
			Capabilities.EnergyStorage.BLOCK,
			this.parent.blockPos,
			this.parent.horizontal
		)?.let { ((it.energyStored.toFloat() / it.maxEnergyStored) * 47).toInt() } ?: 0
	}

	fun getEnergyHandler() : IEnergyStorage? =
		this.parent.level?.getCapability(Capabilities.EnergyStorage.BLOCK, this.parent.blockPos, this.parent.horizontal)

	fun isCrafting() : Boolean = this.parent.progress > 0

	init {
		this.addInventorySlots(inventory, 8, 174, 116)
		if (this.parent.items != null) {
			this.addSlot(SlotItemHandler(this.parent.items, 0, 80, 15))
			this.addSlot(ResultSlotItemHandler(this.parent.items, 1, 80, 87))
		}
	}
}