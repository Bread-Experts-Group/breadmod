package breadmod.block

import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty

class FlourLayeredBlock: FlammableBlock(Properties.copy(Blocks.SNOW)) {
    companion object {
        const val MAX_HEIGHT: Int = 8
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS
    }


}