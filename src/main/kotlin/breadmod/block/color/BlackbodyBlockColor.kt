package breadmod.block.color

import net.minecraft.client.color.block.BlockColor
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

// TODO.
object BlackbodyBlockColor: BlockColor {
    override fun getColor(pState: BlockState, pLevel: BlockAndTintGetter?, pPos: BlockPos?, pTintIndex: Int): Int {
        if(pLevel == null || pPos == null) return 0
        pLevel.getBlockEntity(pPos).let {
            //if(it is HeatingElementBlock.HEBlockEntity) return getRGBFromK(it)
        }
        return 0
    }
}