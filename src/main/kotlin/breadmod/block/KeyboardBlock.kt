package breadmod.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.CandleBlock
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.SimpleWaterloggedBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class KeyboardBlock: Block(Properties.copy(Blocks.IRON_BLOCK).noOcclusion()), SimpleWaterloggedBlock {
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(DirectionalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.WATERLOGGED, false)
        )
    }

    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Deprecated in Java")
    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext,
    ): VoxelShape {
        CandleBlock
        return box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0)
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(DirectionalBlock.FACING, BlockStateProperties.WATERLOGGED) }
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(DirectionalBlock.FACING, pContext.nearestLookingDirection.opposite)
}