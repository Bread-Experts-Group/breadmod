package breadmod.util.capability

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

/**
 * Interface for capabilities that can distribute their contents to surrounding blocks in a level.
 *
 * @author Miko Elbrecht
 * @since 1.0.0
 */
interface ICapabilityDistributable {
    /**
     * Distributes the capability's contents to surrounding blocks.
     * @author Miko Elbrecht
     * @since 1.0.0
     */
    fun distribute(pLevel: Level, pPos: BlockPos, pSides: List<Direction?>?, pFacing: Direction = Direction.NORTH)
}