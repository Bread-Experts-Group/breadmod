package bread.mod.breadmod.neoforge.registry.block

import bread.mod.breadmod.block.BreadBlock
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState

class BreadBlockForge: BreadBlock() {
    override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean = true
    override fun getFlammability(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int = 15
    override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int =
        30
}