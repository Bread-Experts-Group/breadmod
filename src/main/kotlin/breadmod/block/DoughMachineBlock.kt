package breadmod.block

import breadmod.block.entity.DoughMachineBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.FluidTags
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.Items
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.FluidType
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.network.NetworkHooks
import kotlin.jvm.optionals.getOrNull
import kotlin.math.min

class DoughMachineBlock : Block(Properties.of()
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

    override fun canHarvestBlock(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pPlayer: Player): Boolean = !pPlayer.isCreative

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(HorizontalDirectionalBlock.FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(HorizontalDirectionalBlock.FACING, BlockStateProperties.LIT)
    }

    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity = DoughMachineBlockEntity(pPos, pState)
    override fun onBlockExploded(state: BlockState?, level: Level?, pos: BlockPos?, explosion: Explosion?) {
        super.onBlockExploded(state, level, pos, explosion)
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNewState: BlockState,
        pMovedByPiston: Boolean,
    ) {
        if(!pState.`is`(pNewState.block)) {
            val entity = (pLevel.getBlockEntity(pPos) as? DoughMachineBlockEntity) ?: return
            Containers.dropContents(pLevel, pPos, entity)
            super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult,
    ): InteractionResult {
        if(!pLevel.isClientSide) {
            val entity = (pLevel.getBlockEntity(pPos) as? DoughMachineBlockEntity) ?: return InteractionResult.FAIL
            if(pHand == InteractionHand.MAIN_HAND) entity.fluidHandlerOptional.resolve().getOrNull().also {
                if(it == null) return InteractionResult.FAIL

                val stack = pPlayer.getItemInHand(pHand)
                val item = stack.item

                val filled = if(item is BucketItem && item.fluid.`is`(FluidTags.WATER) && it.space(FluidTags.WATER) > 0) {
                    if(!pPlayer.isCreative) pPlayer.setItemInHand(pHand, Items.BUCKET.defaultInstance)
                    it.fill(FluidStack(item.fluid, FluidType.BUCKET_VOLUME), IFluidHandler.FluidAction.EXECUTE)
                } else {
                    FluidUtil.getFluidHandler(stack).resolve().getOrNull().let { stackFluidHandle ->
                        if(stackFluidHandle != null && stackFluidHandle.drain(1, IFluidHandler.FluidAction.SIMULATE).fluid.`is`(FluidTags.WATER)) it.fill(
                            stackFluidHandle.drain(
                                min(FluidType.BUCKET_VOLUME, it.space(FluidTags.WATER)),
                                if(pPlayer.isCreative) IFluidHandler.FluidAction.SIMULATE else IFluidHandler.FluidAction.EXECUTE
                            ),
                            IFluidHandler.FluidAction.EXECUTE
                        ) else 0
                    }
                }
                if(filled > 0) {
                    pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0f, 1.0f)
                    return InteractionResult.CONSUME
                }
            }
            NetworkHooks.openScreen(pPlayer as ServerPlayer, entity, pPos)
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide())
    }

    override fun <T : BlockEntity?> getTicker(pLevel: Level, pState: BlockState, pBlockEntityType: BlockEntityType<T>): BlockEntityTicker<T>? =
        if(pLevel.isClientSide()) null else BlockEntityTicker<T> { _, pPos, _, pBlockEntity -> (pBlockEntity as DoughMachineBlockEntity).tick(pLevel, pPos, pState, pBlockEntity) }
}