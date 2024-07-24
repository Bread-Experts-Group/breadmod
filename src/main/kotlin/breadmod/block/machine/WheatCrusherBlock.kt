package breadmod.block.machine

import breadmod.block.machine.entity.WheatCrusherBlockEntity
import breadmod.registry.block.ModBlockEntityTypes
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks

class WheatCrusherBlock : BaseAbstractMachineBlock.Powered<WheatCrusherBlockEntity>(
    ModBlockEntityTypes.WHEAT_CRUSHER,
    Properties.of()
        .strength(1f, 5.0f)
        .mapColor(MapColor.COLOR_GRAY)
        .sound(SoundType.METAL),
    true
) {
    override fun canHarvestBlock(state: BlockState, level: BlockGetter, pos: BlockPos, player: Player): Boolean = !player.isCreative

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }
    
    override fun onDestroyedByPlayer(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player, 
        pWillHarvest: Boolean,
        pFluid: FluidState
    ): Boolean {
        val entity = (pLevel.getBlockEntity(pPos) as WheatCrusherBlockEntity)
        Containers.dropContents(pLevel, pPos, entity.craftingManager)
        return super.onDestroyedByPlayer(pState, pLevel, pPos, pPlayer, pWillHarvest, pFluid)
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

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<WheatCrusherBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}