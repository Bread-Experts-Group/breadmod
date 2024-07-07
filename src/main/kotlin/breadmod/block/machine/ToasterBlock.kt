package breadmod.block.machine

import breadmod.block.machine.entity.ToasterBlockEntity
import breadmod.registry.block.ModBlockEntities
import breadmod.registry.item.ModItems
import breadmod.util.capability.IndexableItemHandler
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.common.capabilities.ForgeCapabilities

class ToasterBlock : BaseAbstractMachineBlock.Powered<ToasterBlockEntity>(
    ModBlockEntities.TOASTER,
    Properties.of()
        .strength(1f, 3.0f)
        .mapColor(MapColor.TERRACOTTA_WHITE)
        .sound(SoundType.COPPER),
    false
) {
    private val aabbX = box(5.0, 0.0, 2.0, 11.0, 7.0, 14.0)
    private val aabbZ = box(2.0, 0.0, 5.0, 14.0, 7.0, 11.0)
    private val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    private val random = RandomSource.create()

    override fun canHarvestBlock(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pPlayer: Player): Boolean =
        !pPlayer.isCreative
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(BlockStateProperties.TRIGGERED, false)
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.TRIGGERED)
    }

    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.TRIGGERED, false))
        // todo probably don't need this since the state is set on block placement
    }

    @Deprecated("Deprecated in Java")
    override fun getShape(
        pState: BlockState,
        pLevel: BlockGetter,
        pPos: BlockPos,
        pContext: CollisionContext
    ): VoxelShape {
        return when(pState.getValue(facing)) {
            Direction.NORTH, Direction.SOUTH -> aabbX
            else -> aabbZ
        }
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
            val triggered = pState.getValue(BlockStateProperties.TRIGGERED)
            val entity = (pLevel.getBlockEntity(pPos) as? ToasterBlockEntity) ?: return InteractionResult.FAIL
            val itemHandler = entity.capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER) ?: return InteractionResult.FAIL
            val stack = pPlayer.getItemInHand(pHand)
            val item = stack.item

            if(pPlayer.isCrouching && pHand != InteractionHand.OFF_HAND && entity.progress == 0) {
                if(triggered && entity.progress == 0) {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.TRIGGERED, false))
                    pPlayer.sendSystemMessage(Component.literal("toaster not triggered"))
                } else if(!triggered && entity.progress == 0) {
                    pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.TRIGGERED, true))
                    pPlayer.sendSystemMessage(Component.literal("toaster triggered"))
                }
            }

            if(item == ModItems.BREAD_SLICE.get().asItem() &&
                itemHandler[0].count < 2 && !pPlayer.isCrouching && !triggered && entity.progress == 0) {
                if(!pPlayer.isCreative) stack.shrink(1)
                itemHandler.insertItem(ItemStack(item, 1), false)
                pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f, random.nextFloat()-0.3f)
            } else if(!pPlayer.isCrouching && !triggered && entity.progress == 0) {
                Containers.dropContents(pLevel, pPos, entity)
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide)
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
            val entity = (pLevel.getBlockEntity(pPos) as? ToasterBlockEntity) ?: return
            Containers.dropContents(pLevel, pPos, entity)
        }
        @Suppress("DEPRECATION")
        super.onRemove(pState, pLevel, pPos, pNewState, pMovedByPiston)
    }

    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<ToasterBlockEntity> =
        BlockEntityTicker { tLevel, tPos, tState, tBlockEntity -> tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity) }
}