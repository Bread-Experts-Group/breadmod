package breadmod.block

import breadmod.block.entity.DoughMachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty

class DoughMachineBlock : BaseEntityBlock(Properties.of()) {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(FlourMachineEnums.running, false)
        )
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FlourMachineEnums.facing, pContext.horizontalDirection.opposite) as BlockState
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(FlourMachineEnums.facing, FlourMachineEnums.running)
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity {
        return DoughMachineBlockEntity(pPos, pState)
    }

    override fun rotate(pState: BlockState, level: LevelAccessor, pos: BlockPos, pRotation: Rotation): BlockState {
        return pState.setValue(BlockStateProperties.FACING, pState.getValue(FlourMachineEnums.facing))
    }

    object FlourMachineEnums {
        val running: BooleanProperty = BooleanProperty.create("running")
        val facing: DirectionProperty = HorizontalDirectionalBlock.FACING
    }
}