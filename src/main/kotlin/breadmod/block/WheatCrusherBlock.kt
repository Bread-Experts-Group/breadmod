package breadmod.block

import breadmod.block.entity.WheatCrusherBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class WheatCrusherBlock: Block(Properties.of()
    .strength(1f)
    .mapColor(MapColor.COLOR_GRAY)
    .sound(SoundType.METAL)), EntityBlock
{
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.LIT, false)
        )
    }

    override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean = !player.isCreative
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity = WheatCrusherBlockEntity(pPos, pState)

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(HorizontalDirectionalBlock.FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(HorizontalDirectionalBlock.FACING, BlockStateProperties.LIT)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)",
        "net.minecraft.world.level.block.Block"
    ))
    override fun onRemove(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNewState: BlockState,
        pMovedByPiston: Boolean
    ) {
        if(!pState.`is`(pNewState.block)) {
            val entity = (pLevel.getBlockEntity(pPos) as? WheatCrusherBlockEntity) ?: return
            Containers.dropContents(pLevel, pPos, entity)
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        if(!pLevel.isClientSide) {
            val entity = (pLevel.getBlockEntity(pPos) as? WheatCrusherBlockEntity) ?: return InteractionResult.FAIL
            NetworkHooks.openScreen(pPlayer as ServerPlayer, entity, pPos)
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide())
    }

    override fun <T : BlockEntity?> getTicker(
        pLevel: Level,
        pState: BlockState,
        pBlockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? =
        if(pLevel.isClientSide()) null else BlockEntityTicker<T> { _, pPos, _, pBlockEntity -> (pBlockEntity as WheatCrusherBlockEntity).tick(pLevel, pPos, pState, pBlockEntity) }
}