package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.tags.FluidTags
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import net.neoforged.neoforge.items.SlotItemHandler
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.menu.ModMenuTypes
import org.bread_experts_group.breadmod.registry.recipe.actual.DoughMachineRecipe
import org.bread_experts_group.breadmod.util.isTag
import kotlin.jvm.optionals.getOrNull

class DoughMachineMenu(
	id: Int,
	inventory: Inventory,
	parent: DoughMachineBlockEntity
) : BMContainerMenu.RecipeEntity<DoughMachineRecipe, DoughMachineBlockEntity>(
	ModMenuTypes.DOUGH_MACHINE.get(),
	id,
	inventory,
	parent
) {
	constructor(id: Int, inventory: Inventory, byteBuf: RegistryFriendlyByteBuf) : this(
		id, inventory,
		BMContainerMenu.blockEntityFromByteBuf(inventory, byteBuf, ModBlockEntityTypes.DOUGH_MACHINE)
	)

	override val progressWidth: Int = 24
	override val containerSlotCount: Int = 4

	private inner class DoughMachineBucketSlot : SlotItemHandler(this.parent.itemHandler, 3, 153, 7) {
		override fun mayPlace(stack: ItemStack): Boolean =
			stack.item.let { it is BucketItem && isTag(FluidTags.WATER) } ||
					FluidUtil.getFluidHandler(stack).getOrNull().let {
						it?.drain(1, IFluidHandler.FluidAction.SIMULATE)
							?.let { drained -> drained.amount == 1 && isTag(FluidTags.WATER) } == true
					}
	}

	init {
		this.addInventorySlots(inventory, 8, 142, 84)
		this.addHandlerSlot(0, 10, 34)
		this.addHandlerSlot(1, 45, 34)
		this.addResultHandlerSlot(2, 98, 35)
		this.addSlot(this.DoughMachineBucketSlot())
	}
}