package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.IItemHandler
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.util.isTag
import kotlin.jvm.optionals.getOrNull

class DoughMachineMenu(
	id: Int,
	inventory: Inventory,
	val parent: DoughMachineBlockEntity
) : AbstractModContainerMenu<DoughMachineBlockEntity>(ModMenuTypes.DOUGH_MACHINE.get(), id, parent) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		inventory.player.level().getBlockEntity(byteBuf.readBlockPos(), ModBlockEntityTypes.DOUGH_MACHINE.get()).get()
	)

	val scaledProgress: Int
		get() = ((this.parent.progress.toFloat() / this.parent.maxProgress.toFloat()) * 24).toInt()
	val energyStoredScaled: Int
		get() = this.getEnergyHandler()
			.let { ((it.energyStoredDecimal / it.maxEnergyStoredDecimal).toFloat() * 47).toInt() }

	fun isCrafting(): Boolean = this.parent.progress > 0
	override val containerSlotCount: Int = 3

	class DoughMachineBucketSlot(handler: IItemHandler) : SlotItemHandler(handler, 2, 153, 7) {
		override fun mayPlace(stack: ItemStack): Boolean =
			stack.item.let { it is BucketItem && isTag(FluidTags.WATER) } ||
					FluidUtil.getFluidHandler(stack).getOrNull().let {
						it?.drain(1, IFluidHandler.FluidAction.SIMULATE)
							?.let { drained -> drained.amount == 1 && isTag(FluidTags.WATER) } == true
					}
	}

	init {
		this.addInventorySlots(inventory, 8, 142, 84)
		this.addSlot(SlotItemHandler(this.parent.itemHandler, 0, 26, 34))
		this.addSlot(ResultSlotItemHandler(this.parent.itemHandler, 1, 78, 35))
		this.addSlot(DoughMachineBucketSlot(this.parent.itemHandler))
	}
}