package bread.mod.breadmod.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class CharacterModelBlock : Block(Properties.of().noOcclusion()) {
    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState = defaultBlockState()
        .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection)

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.getShape(pState, pLevel, pPos, pContext)",
            "net.minecraft.world.level.block.Block"
        )
    )
    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape = when (pState.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
        Direction.NORTH, Direction.SOUTH -> box(0.0, 0.0, 3.0, 16.0, 20.0, 13.0)
        else -> box(3.0, 0.0, 0.0, 13.0, 20.0, 16.0)
    }
}