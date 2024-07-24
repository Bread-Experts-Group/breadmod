package breadmod.block.util

import net.minecraftforge.common.extensions.IForgeFluid

/**
 * Represents a liquid that can be used as fuel (has burn time.)
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface ILiquidCombustible : IForgeFluid {
    /**
     * Returns the burn time of this liquid, in ticks.
     *
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun getBurnTime(): Int
}