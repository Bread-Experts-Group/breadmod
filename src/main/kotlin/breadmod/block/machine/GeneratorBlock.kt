package breadmod.block.machine

import breadmod.block.machine.entity.GeneratorBlockEntity
import breadmod.block.util.smokeAtEdge
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.RandomSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.phys.BlockHitResult
import net.minecraftforge.common.ForgeHooks

class GeneratorBlock: BaseAbstractMachineBlock.Toggleable<GeneratorBlockEntity>(
    ModBlockEntities.GENERATOR,
    Properties.of().noOcclusion()
        .strength(1.5f, 5.0f)
        .sound(SoundType.METAL)
        .lightLevel { state -> if(state.getValue(BlockStateProperties.LIT)) 8 else 0 }
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.LIT)
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
        if(pLevel.isClientSide || pHand == InteractionHand.OFF_HAND) return InteractionResult.CONSUME
        val handStack = pPlayer.getItemInHand(pHand)
        val burnTime = ForgeHooks.getBurnTime(handStack, null)

        return if(handStack.item !is BucketItem && burnTime > 0) {
            val blockEntity = pLevel.getBlockEntity(pPos) as? GeneratorBlockEntity ?: return InteractionResult.FAIL
            if(blockEntity.addBurnTime(burnTime)) {
                if(!pPlayer.isCreative) handStack.shrink(1)
                InteractionResult.SUCCESS
            } else InteractionResult.FAIL
        } else InteractionResult.FAIL
    }

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        if (pState.getValue(BlockStateProperties.LIT))
            smokeAtEdge(
                pLevel, pPos,
                ParticleTypes.CAMPFIRE_COSY_SMOKE, SoundEvents.FIRE_AMBIENT,
                5 to 8, pState.getValue(BlockStateProperties.HORIZONTAL_FACING)
            )
    }

    override fun onDestroyedByPlayer(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pWillHarvest: Boolean,
        pFluid: FluidState
    ): Boolean {
        val entity = (pLevel.getBlockEntity(pPos) as GeneratorBlockEntity)
        Containers.dropContents(pLevel, pPos, entity.cManager)
        return super.onDestroyedByPlayer(pState, pLevel, pPos, pPlayer, pWillHarvest, pFluid)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
            .setValue(BlockStateProperties.LIT, false)
    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<GeneratorBlockEntity> =
        BlockEntityTicker { tLevel, pPos, tState, pBlockEntity -> pBlockEntity.tick(tLevel, pPos, tState, pBlockEntity) }
}