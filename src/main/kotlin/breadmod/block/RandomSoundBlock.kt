package breadmod.block

import breadmod.ModMain.modTranslatable
import net.minecraft.ChatFormatting
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.MapColor
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.registries.ForgeRegistries

class RandomSoundBlock: Block(Properties.of()
    .strength(4f, 6f)
    .mapColor(MapColor.COLOR_GRAY)
    .requiresCorrectToolForDrops()
    .sound(SoundType.METAL)
) {
    val random = RandomSource.create()

    init {
        registerDefaultState(defaultBlockState().setValue(BlockStateProperties.POWERED, false))
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState =
        defaultBlockState()
            .setValue(BlockStateProperties.POWERED, false)

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.POWERED)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.use(pState, pLevel, pPos, pPlayer, pHand, pHit)",
        "net.minecraft.world.level.block.Block"
    ))
    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        if (pHand != InteractionHand.OFF_HAND && !pLevel.isClientSide) playRandomSound(pPos, pLevel)
        return InteractionResult.sidedSuccess(pLevel.isClientSide)
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
        val randomEntry = ForgeRegistries.SOUND_EVENTS.entries.random().value
        pLevel.playSound(null, pPos, randomEntry, SoundSource.RECORDS, 1f, 1f)
        pLevel.blockEvent(pPos, this, 0, 0)
    }

    @Deprecated("Deprecated in Java", ReplaceWith(
        "super.triggerEvent(pState, pLevel, pPos, pId, pParam)",
        "net.minecraft.world.level.block.Block"
    ))
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
        pStack: ItemStack,
        pLevel: BlockGetter?,
        pTooltip: MutableList<Component>,
        pFlag: TooltipFlag
    ) {
        pTooltip.add(
            modTranslatable("sound_block", "tooltip")
            .withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC)
        )
    }
}
