package bread.mod.breadmod.block

import bread.mod.breadmod.CommonUtils.invalidateCaps
import bread.mod.breadmod.ModMainCommon.modTranslatable
import bread.mod.breadmod.block.entity.ToasterBlockEntity
import bread.mod.breadmod.registry.block.ModBlockEntityTypes
import bread.mod.breadmod.registry.tag.ItemTags
import com.mojang.serialization.MapCodec
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
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class ToasterBlock : BaseEntityBlock(
    Properties.of()
        .strength(1f, 1.0f)
        .mapColor(MapColor.TERRACOTTA_WHITE)
        .sound(SoundType.COPPER)
) {
    val codec: MapCodec<ToasterBlock> = simpleCodec { this }
    private val aabbX = box(5.0, 0.0, 2.0, 11.0, 7.0, 14.0)
    private val aabbZ = box(2.0, 0.0, 5.0, 14.0, 7.0, 11.0)
    private val facing: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    private val random = RandomSource.create()
    private val triggered = BlockStateProperties.TRIGGERED

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(triggered, false)
            .setValue(BlockStateProperties.WATERLOGGED, false)

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(
            BlockStateProperties.HORIZONTAL_FACING,
            BlockStateProperties.TRIGGERED,
            BlockStateProperties.WATERLOGGED
        )
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext
    ): VoxelShape =
        when (state.getValue(facing)) {
            Direction.NORTH, Direction.SOUTH -> aabbX
            else -> aabbZ
        }

    override fun animateTick(state: BlockState, level: Level, pos: BlockPos, random: RandomSource) {
        val entity = level.getBlockEntity(pos) as? ToasterBlockEntity ?: return
        val itemHandlerSlot = entity.getItem(0)
        val posX = pos.x + 0.4
        val posY = pos.y + 0.5
        val posZ = pos.z + 0.5

        val direction = state.getValue(facing)
        val axis = direction.axis
        val d1 = random.nextDouble() * 0.6 - 0.3
        val d4 = if (axis == Direction.Axis.X) direction.stepZ * 0.52 else d1 // X
        val d3 = random.nextDouble() * 0.6 / 16.0 // Y
        val d2 = if (axis == Direction.Axis.Z) direction.stepX * 0.52 else d1 // Z
        if (state.getValue(triggered)) {
            val negativeFlag = posZ + d4 + if (axis == Direction.Axis.X) -0.1 else 0.0
            val positiveFlag = posZ + d4 + if (axis == Direction.Axis.X) -0.1 else 0.0
            if (itemHandlerSlot.`is`(Items.CHARCOAL)) {
                level.addParticle(
                    ParticleTypes.LAVA,
                    posX + d2,
                    posY + d3,
                    negativeFlag,
                    0.0, 0.0, 0.0
                )
                level.addParticle(
                    ParticleTypes.LAVA,
                    posX + d2 + 0.2,
                    posY + d3,
                    positiveFlag,
                    0.0, 0.0, 0.0
                )
            } else {
                level.addParticle(
                    ParticleTypes.SMOKE,
                    posX + d2,
                    posY + d3,
                    negativeFlag,
                    0.0, 0.0, 0.0
                )
                level.addParticle(
                    ParticleTypes.SMOKE,
                    posX + d2 + 0.2,
                    posY + d3,
                    positiveFlag,
                    0.0, 0.0, 0.0
                )
            }
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: Item.TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(modTranslatable("block", "toaster", "tooltip").withStyle(ChatFormatting.RED))
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = codec

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult
    ): ItemInteractionResult {
        val triggeredState = state.getValue(triggered)
        val entity = (level.getBlockEntity(pos) as? ToasterBlockEntity) ?: return ItemInteractionResult.FAIL
        val itemHandler = entity.getItem(0)
        val handStack = player.getItemInHand(hand)

        if (!triggeredState && entity.progress == 0 && itemHandler.count != 2 && handStack.`is`(ItemTags.TOASTABLE)) {
            if (!player.isCreative) handStack.shrink(1)
            // todo actual item inserting
            entity.setItem(0, ItemStack(handStack.item, 2))
            level.playSound(
                null,
                pos,
                SoundEvents.ITEM_PICKUP,
                SoundSource.BLOCKS,
                0.2f,
                random.nextFloat() - 0.3f
            )
            entity.setChanged()
        }

        return ItemInteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        val triggeredState = state.getValue(triggered)
        val entity = (level.getBlockEntity(pos) as? ToasterBlockEntity) ?: return InteractionResult.FAIL

        if (player.isCrouching && entity.progress == 0) {
            if (triggeredState) {
                updateState(level, pos, state, triggered, false)
            } else if (!triggeredState) {
                updateState(level, pos, state, triggered, true)
            }
        } else if (!player.isCrouching && !triggeredState && entity.progress == 0) {
            Containers.dropContents(level, pos, entity.items)
            entity.setChanged()
        }

        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (!state.`is`(newState.block)) {
            val entity = (level.getBlockEntity(pos) as? ToasterBlockEntity) ?: return
            Containers.dropContents(level, pos, entity.items)
        }
        // actual working block invalidation????
        level.invalidateCaps(pos)
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun newBlockEntity(
        pos: BlockPos,
        state: BlockState
    ): BlockEntity = ToasterBlockEntity(pos, state)

    override fun <T : BlockEntity> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        blockEntityType,
        ModBlockEntityTypes.TOASTER.get()
    ) { tLevel: Level, tPos: BlockPos, tState: BlockState, tBlockEntity: ToasterBlockEntity ->
        tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity)
    }

    private fun <T : Comparable<T>, V : T> updateState(
        level: Level,
        pos: BlockPos,
        state: BlockState,
        value: Property<T>,
        boolean: V
    ) = level.setBlockAndUpdate(pos, state.setValue(value, boolean))
}