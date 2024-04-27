package breadmod.block

import breadmod.block.entity.DoughMachineBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class DoughMachineBlock : BaseEntityBlock(Properties.of()) {
    init {
        this.registerDefaultState(
            stateDefinition.any()
                .setValue(HorizontalDirectionalBlock.FACING, Direction.NORTH)
                .setValue(DoughMachineEnums.running, false)
        )
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(DoughMachineEnums.facing, pContext.horizontalDirection.opposite) as BlockState
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(DoughMachineEnums.facing, DoughMachineEnums.running)
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity {
        return DoughMachineBlockEntity(pPos, pState)
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(pState: BlockState, pLevel: Level, pPos: BlockPos, pNewState: BlockState, pMovedByPiston: Boolean) {
        if(pState.block != pNewState.block) {
            val blockEntity = pLevel.getBlockEntity(pPos)
            if(blockEntity is DoughMachineBlockEntity) {
                blockEntity.drops()
            }
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
            val entity = pLevel.getBlockEntity(pPos)
            if(entity is DoughMachineBlockEntity) {
                NetworkHooks.openScreen(pPlayer as ServerPlayer, entity, pPos)
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide())
    }

    override fun <T : BlockEntity?> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? {
        if(pLevel.isClientSide()) {
            return null
        }

        return createTickerHelper(pBlockEntityType, ModBlockEntities.DOUGH_MACHINE.get()
        ) { pLevel1: Level, pPos: BlockPos, pState1: BlockState, pBlockEntity: DoughMachineBlockEntity ->
            pBlockEntity.tick(pLevel1, pPos, pState1) }
    }

    override fun rotate(pState: BlockState, level: LevelAccessor, pos: BlockPos, pRotation: Rotation): BlockState {
        return pState.setValue(BlockStateProperties.FACING, pState.getValue(DoughMachineEnums.facing))
    }

    @Deprecated("Deprecated in Java", ReplaceWith("RenderShape.MODEL", "net.minecraft.world.level.block.RenderShape"))
    override fun getRenderShape(pState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    object DoughMachineEnums {
        val running: BooleanProperty = BooleanProperty.create("running")
        val facing: DirectionProperty = HorizontalDirectionalBlock.FACING
    }
}