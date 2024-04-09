package breadmod.block

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.phys.shapes.VoxelShape

interface ILayeredBlock {
    companion object {
        const val MAX_HEIGHT: Int = 8
        val LAYERS: IntegerProperty = BlockStateProperties.LAYERS

        private var index = 0
        val SHAPE_BY_LAYER: List<VoxelShape> = buildList(8) {
            Block.box(0.0, 0.0, 0.0, 16.0, (index++) * 2.0, 16.0) }
        // TODO
    }
}