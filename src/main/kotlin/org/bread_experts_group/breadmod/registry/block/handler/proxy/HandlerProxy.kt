package org.bread_experts_group.breadmod.registry.block.handler.proxy

abstract class HandlerProxy<T>(
	protected val parent: T,
	/**
	 * Maps this proxy's slots to the target slots of the [parent].
	 */
	protected vararg val map: Pair<Int, Int>
) {
	protected fun redirectSlot(slot: Int): Int = this.map.first { (first, _) -> slot == first }.second
}