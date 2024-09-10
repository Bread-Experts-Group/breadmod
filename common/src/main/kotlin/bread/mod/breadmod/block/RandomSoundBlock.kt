package bread.mod.breadmod.block

import bread.mod.breadmod.ModMainCommon.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult

class RandomSoundBlock : Block(
    Properties.of()
        .strength(4f, 6f)
        .mapColor(MapColor.COLOR_GRAY)
        .requiresCorrectToolForDrops()
        .sound(SoundType.METAL)
) {
    val random: RandomSource = RandomSource.create()

    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false))
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.POWERED, false)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.POWERED)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (player.usedItemHand == InteractionHand.MAIN_HAND && !level.isClientSide) playRandomSound(pos, level)
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    @Deprecated("Deprecated in Java")
    override fun neighborChanged(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pNeighborBlock: Block,
        pNeighborPos: BlockPos,
        pMovedByPiston: Boolean
    ) {
        val flag = pLevel.hasNeighborSignal(pPos)
        if (flag != pState.getValue(BlockStateProperties.POWERED)) {
            if (flag) playRandomSound(pPos, pLevel)
            pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, flag))
        }
    }

    private fun playRandomSound(pPos: BlockPos, pLevel: Level) {
        val randomEntry = BuiltInRegistries.SOUND_EVENT.entrySet().random().value
        pLevel.playSound(null, pPos, randomEntry, SoundSource.RECORDS, 1f, 1f)
        pLevel.blockEvent(pPos, this, 0, 0)
    }

    @Deprecated(
        "Deprecated in Java", ReplaceWith(
            "super.triggerEvent(pState, pLevel, pPos, pId, pParam)",
            "net.minecraft.world.level.block.Block"
        )
    )
    override fun triggerEvent(pState: BlockState, pLevel: Level, pPos: BlockPos, pId: Int, pParam: Int): Boolean {
        pLevel.addParticle(
            ParticleTypes.NOTE,
            pPos.x.toDouble() + 0.5,
            pPos.y.toDouble() + 1.2,
            pPos.z.toDouble() + 0.5,
            random.nextDouble() % 24,
            0.0,
            0.0
        )
        return true
    }

    override fun appendHoverText(
        stack: ItemStack,
        context: Item.TooltipContext,
        tooltipComponents: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        tooltipComponents.add(
            modTranslatable("sound_block", "tooltip")
                .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        )
    }
}