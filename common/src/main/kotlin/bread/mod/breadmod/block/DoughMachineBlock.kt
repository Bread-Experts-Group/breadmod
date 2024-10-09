package bread.mod.breadmod.block

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor

class DoughMachineBlock : Block(
    Properties.of()
        .mapColor(MapColor.COLOR_GRAY)
        .strength(1f, 5.0f)
        .sound(SoundType.METAL)
//        .lightLevel { state -> if (state.getValue(BlockStateProperties.POWERED)) 5 else 0 }
//        .emissiveRendering { state, _, _ -> state.getValue(BlockStateProperties.POWERED) }
) {
    // todo yeah..

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
    }
}