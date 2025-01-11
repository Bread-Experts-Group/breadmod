package org.bread_experts_group.breadmod.registry.block.actual.util

import net.neoforged.neoforge.common.extensions.IFluidExtension

/**
 * Represents a liquid that can be used as fuel (has burn time.)
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface ILiquidCombustible : IFluidExtension {
	/**
	 * Returns the burn time of this liquid, in ticks.
	 *
	 * @author Miko Elbrecht
	 * @since 1.0.0
	 */
	fun getBurnTime(): Int
}