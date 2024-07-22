package breadmod.util.capability

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level

interface ICapabilityDistributable {
    fun distribute(pLevel: Level, pPos: BlockPos, sides: List<Direction?>?, facing: Direction = Direction.NORTH)
}