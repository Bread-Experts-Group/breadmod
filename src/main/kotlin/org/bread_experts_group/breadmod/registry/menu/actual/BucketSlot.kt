package org.bread_experts_group.breadmod.registry.menu.actual

import net.minecraft.tags.FluidTags
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.fluids.FluidUtil
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE
import org.bread_experts_group.breadmod.util.handlers.ExpansibleItemHandler
import org.bread_experts_group.breadmod.util.isTag
import kotlin.jvm.optionals.getOrNull

class BucketSlot(handler: ExpansibleItemHandler) : ModifiedSlotItemHandler(handler, 3, 153, 7) {
	override fun mayPlace(stack: ItemStack): Boolean =
		stack.item.let { it is BucketItem && isTag(FluidTags.WATER) /* todo returns false for some reason */ } ||
				FluidUtil.getFluidHandler(stack).getOrNull().let {
					it?.drain(1, SIMULATE)
						?.let { drained -> drained.amount == 1 && isTag(FluidTags.WATER) } == true
				}
}