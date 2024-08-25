package breadmod.block.multiblock.farmer

import breadmod.block.entity.multiblock.farmer.FarmerInputBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.Containers
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DirectionalBlock
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.MapColor

class FarmerInputBlock : Block(
    Properties.of()
        .strength(2.0f, 6.0f)
        .mapColor(MapColor.COLOR_BROWN)
        .sound(SoundType.COPPER)
), EntityBlock {

    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(DirectionalBlock.FACING, Direction.NORTH)
        )
    }

    override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean =
        !player.isCreative

    @Deprecated("Deprecated in Java")
    override fun onRemove(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNewState: BlockState,
        pMovedByPiston: Boolean
    ) {
        if (!pState.`is`(pNewState.block)) {
            val entity = (pLevel.getBlockEntity(pPos) as? FarmerInputBlockEntity) ?: return
            Containers.dropContents(pLevel, pPos, entity)
        }
        @Suppress("DEPRECATION")
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(DirectionalBlock.FACING, pContext.nearestLookingDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(DirectionalBlock.FACING)
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity =
        FarmerInputBlockEntity(pPos, pState)

}