package breadmod.block

import breadmod.block.entity.BreadScreenBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class MonitorBlock: Block(Properties.copy(Blocks.IRON_BLOCK)), EntityBlock {
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(DirectionalBlock.FACING, Direction.NORTH)
        )
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity = BreadScreenBlockEntity(pPos, pState)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(DirectionalBlock.FACING) }
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState().setValue(DirectionalBlock.FACING, pContext.nearestLookingDirection)
}