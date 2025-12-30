package org.bread_experts_group.breadmod.registry.block.handler.proxy

import net.neoforged.neoforge.fluids.FluidStack
import net.neoforged.neoforge.fluids.capability.IFluidHandler
import org.bread_experts_group.breadmod.registry.block.handler.ExtendedFluidHandler

class ExtendedFluidHandlerProxy(
	parent: ExtendedFluidHandler,
	vararg tankMap: Pair<Int, Int>
) : HandlerProxy<ExtendedFluidHandler>(parent, *tankMap), IFluidHandler {
	override fun getTanks(): Int = this.map.size
	override fun getFluidInTank(tank: Int): FluidStack = this.parent.getFluidInTank(this.redirectSlot(tank))
	override fun getTankCapacity(tank: Int): Int = this.parent.getTankCapacity(this.redirectSlot(tank))

	override fun isFluidValid(tank: Int, stack: FluidStack): Boolean =
		this.parent.isFluidValid(this.redirectSlot(tank), stack)

	override fun fill(
		resource: FluidStack,
		action: IFluidHandler.FluidAction
	): Int = this.parent.fill(resource, action)

	override fun drain(
		resource: FluidStack,
		action: IFluidHandler.FluidAction
	): FluidStack = this.parent.drain(resource, action)

	override fun drain(
		maxDrain: Int,
		action: IFluidHandler.FluidAction
	): FluidStack = this.parent.drain(maxDrain, action)
}