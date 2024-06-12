package breadmod.block.machine.multiblock.generic

import breadmod.block.machine.multiblock.generic.entity.PowerInterfaceBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.MapColor

class PowerInterfaceBlock: DirectionalBlock(Properties.of()
    .strength(2.0f, 6.0f)
    .mapColor(MapColor.COLOR_BROWN)
    .sound(SoundType.COPPER)
), EntityBlock {
    init {
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(FACING, pContext.nearestLookingDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(FACING)
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity = PowerInterfaceBlockEntity(pPos, pState)
}