package breadmod.block.machine

import breadmod.ModMain
import breadmod.block.machine.entity.ToasterBlockEntity
import breadmod.registry.block.ModBlockEntities
import breadmod.util.capability.IndexableItemHandler
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
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
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.minecraftforge.common.capabilities.ForgeCapabilities

class ToasterBlock : BaseAbstractMachineBlock.Powered<ToasterBlockEntity>(
    ModBlockEntities.TOASTER,
    Properties.of()
        .strength(1f, 1.0f)
        .mapColor(MapColor.TERRACOTTA_WHITE)
        .sound(SoundType.COPPER),
    false
) {
    private val aabbX = box(5.0, 0.0, 2.0, 11.0, 7.0, 14.0)
    private val aabbZ = box(2.0, 0.0, 5.0, 14.0, 7.0, 11.0)
    private val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    private val random = RandomSource.create()
    private val triggered = BlockStateProperties.TRIGGERED

    override fun canHarvestBlock(pState: BlockState, pLevel: BlockGetter, pPos: BlockPos, pPlayer: Player): Boolean =
        !pPlayer.isCreative
    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(triggered, false)
            .setValue(BlockStateProperties.WATERLOGGED, false)
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.TRIGGERED,
            BlockStateProperties.WATERLOGGED
        )
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

    // todo yeeterson idea, waterlogged toaster that will zap and kill you if you activate it
    // todo (figure out lightning particle or model that starts from the toaster to the player)
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
            val triggeredState = pState.getValue(triggered)
            val entity = (pLevel.getBlockEntity(pPos) as? ToasterBlockEntity) ?: return InteractionResult.FAIL
            val itemHandler = entity.capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER) ?: return InteractionResult.FAIL
            val itemHandlerSlot = itemHandler.getStackInSlot(0)
            val stack = pPlayer.getItemInHand(pHand)
            val item = stack.item

            if(pPlayer.isCrouching && pHand != InteractionHand.OFF_HAND && entity.progress == 0) {
                if(triggeredState && entity.progress == 0) {
                    updateState(pLevel, pPos, pState, triggered, false)
                } else if(!triggeredState && entity.progress == 0) {
                    updateState(pLevel, pPos, pState, triggered, true)
                }
            }

            if(!pPlayer.isCrouching && !triggeredState && entity.progress == 0 && !stack.isEmpty && itemHandlerSlot.count != 2) {
                if(!pPlayer.isCreative) stack.shrink(1)
                itemHandler.insertItem(0, ItemStack(item, 1), false)
                pLevel.playSound(null, pPos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 0.2f, random.nextFloat()-0.3f)
                entity.setChanged()
            } else if(!pPlayer.isCrouching && !triggeredState && entity.progress == 0) {
                Containers.dropContents(pLevel, pPos, entity)
                entity.setChanged()
            }
        }

        return InteractionResult.sidedSuccess(pLevel.isClientSide)
    }

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        val entity = pLevel.getBlockEntity(pPos) as? ToasterBlockEntity ?: return
        val itemHandler = entity.capabilityHolder.capabilityOrNull<IndexableItemHandler>(ForgeCapabilities.ITEM_HANDLER) ?: return
        val itemHandlerSlot = itemHandler.getStackInSlot(0)
        val posX = pPos.x + 0.4
        val posY = pPos.y + 0.5
        val posZ = pPos.z + 0.5

        val direction = pState.getValue(facing)
        val axis = direction.axis
        val d1 = pRandom.nextDouble() * 0.6 - 0.3
        val d4 = if(axis == Direction.Axis.X) direction.stepZ * 0.52 else d1 // X
        val d3 = pRandom.nextDouble() * 0.6 / 16.0 // Y
        val d2 = if(axis == Direction.Axis.Z) direction.stepX * 0.52 else d1 // Z
        if(pState.getValue(triggered)) {
            if(itemHandlerSlot.`is`(Items.CHARCOAL)) {
                pLevel.addParticle(
                    ParticleTypes.LAVA,
                    posX + d2,
                    posY + d3,
                    posZ + d4 + if(axis == Direction.Axis.X) -0.1 else 0.0,
                    0.0, 0.0, 0.0
                )
                pLevel.addParticle(
                    ParticleTypes.LAVA,
                    posX + d2 + 0.2,
                    posY + d3,
                    posZ + d4 + if(axis == Direction.Axis.X) 0.1 else 0.0,
                    0.0, 0.0, 0.0
                )
            } else {
                pLevel.addParticle(
                    ParticleTypes.SMOKE,
                    posX + d2,
                    posY + d3,
                    posZ + d4 + if(axis == Direction.Axis.X) -0.1 else 0.0,
                    0.0, 0.0, 0.0
                )
                pLevel.addParticle(
                    ParticleTypes.SMOKE,
                    posX + d2 + 0.2,
                    posY + d3,
                    posZ + d4 + if(axis == Direction.Axis.X) 0.1 else 0.0,
                    0.0, 0.0, 0.0
                )
            }
        }
    }

    override fun appendHoverText(
        pStack: ItemStack,
        pLevel: BlockGetter?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        pTooltip.add(1, ModMain.modTranslatable("toaster", "tooltip").withStyle(ChatFormatting.RED))
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
    
    private fun <T: Comparable<T>, V:T> updateState(
        pLevel: Level, 
        pPos: BlockPos, 
        pState: BlockState, 
        pValue: Property<T>, 
        boolean: V
    ) = pLevel.setBlockAndUpdate(pPos, pState.setValue(pValue, boolean))
}