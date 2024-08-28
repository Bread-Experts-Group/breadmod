package breadmod.block

import breadmod.block.entity.SoundBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.network.NetworkHooks
import net.minecraftforge.registries.ForgeRegistries

class SoundBlock: BaseEntityBlock(Properties.of()) {
    override fun newBlockEntity(pPos: BlockPos, pState: BlockState): BlockEntity =
        SoundBlockEntity(pPos, pState)

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    init {
        registerDefaultState(stateDefinition.any()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH)
        )
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("RenderShape.MODEL", "net.minecraft.world.level.block.RenderShape")
    )
    override fun getRenderShape(pState: BlockState): RenderShape = RenderShape.MODEL

    @Deprecated("Deprecated in Java")
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        if (!pLevel.isClientSide) {
            val entity = (pLevel.getBlockEntity(pPos) as SoundBlockEntity)
            if (pPlayer.isShiftKeyDown) {
                entity.currentSound?.let {
                    val sound = ForgeRegistries.SOUND_EVENTS.getValue(ResourceLocation(it)) ?: return InteractionResult.PASS
                    pLevel.playSound(null, pPos, sound, SoundSource.BLOCKS, 1f, 1f)
                }
            } else {
                NetworkHooks.openScreen(pPlayer as ServerPlayer, entity, pPos)
            }
        }
        return InteractionResult.sidedSuccess(pLevel.isClientSide)
    }
}