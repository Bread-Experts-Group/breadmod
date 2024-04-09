package breadmod.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor

class FlourBlock: FlammableBlock(Properties.copy(Blocks.SNOW_BLOCK).ignitedByLava().mapColor(MapColor.COLOR_YELLOW)) {
    override fun getFlammability(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 100
    override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 150
}
