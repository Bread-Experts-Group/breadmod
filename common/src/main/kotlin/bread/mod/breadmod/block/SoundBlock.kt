package bread.mod.breadmod.block

import bread.mod.breadmod.block.entity.SoundBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult

class SoundBlock : Block(
    Properties.of()
        .strength(4f, 6f)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.METAL)
), EntityBlock {
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity =
        SoundBlockEntity(pPos, pState)

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    init {
        registerDefaultState(
            stateDefinition.any()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("RenderShape.MODEL", "net.minecraft.world.level.block.RenderShape")
    )
    override fun getRenderShape(pState: BlockState): RenderShape = RenderShape.MODEL

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val entity = (level.getBlockEntity(pos) as SoundBlockEntity)
            if (player.isShiftKeyDown) {
                // todo figure this out later
                /*                entity.currentSound?.let {
                                    val sound = BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.fromNamespaceAndPath(it)) ?:
                                    return InteractionResult.PASS
                                    pLevel.playSound(null, pPos, sound, SoundSource.BLOCKS, 1f, 1f)
                                }*/
                entity.currentSound?.let {
                    val sound = BuiltInRegistries.SOUND_EVENT.get(
                        ResourceLocation.fromNamespaceAndPath(
                            it.location.namespace,
                            it.location.path
                        )
                    )
                    if (sound != null) {
                        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1f, 1f)
                    }
                }
            } else {
//                NetworkHooks.openScreen(pPlayer as ServerPlayer, entity, pPos)
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }
}