package breadmod.block.machine

import breadmod.block.machine.entity.GeneratorBlockEntity
import breadmod.registry.block.ModBlockEntities
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.RandomSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.div
import thedarkcolour.kotlinforforge.forge.vectorutil.v3d.toVector3d

class GeneratorBlock: BaseAbstractMachineBlock.Powered<GeneratorBlockEntity>(
    ModBlockEntities.GENERATOR,
    Properties.of().noOcclusion()
        .strength(1.5f, 5.0f)
        .sound(SoundType.METAL),
    false
) {
    override fun adjustBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.HORIZONTAL_FACING)
    }

    override fun use(
        pState: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        pPlayer: Player,
        pHand: InteractionHand,
        pHit: BlockHitResult
    ): InteractionResult {
        if(pLevel.isClientSide) return InteractionResult.PASS


        return InteractionResult.CONSUME
    }

    override fun animateTick(pState: BlockState, pLevel: Level, pPos: BlockPos, pRandom: RandomSource) {
        if (pState.getValue(BlockStateProperties.POWERED)) {
            repeat(pRandom.nextInt(3, 8)) {
                val facing = pState.getValue(BlockStateProperties.HORIZONTAL_FACING)
                val normal = facing.opposite.normal
                val from = pPos.offset(normal.div(when(facing) {
                    Direction.WEST, Direction.NORTH -> 1
                    else -> 2
                })).toVector3d()
                val normalDouble = normal.toVector3d()
                pLevel.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    from.x, pPos.y + 0.25, from.z,
                    (normalDouble.x + (pRandom.nextDouble() - 0.5)) / 20,
                    0.1,
                    (normalDouble.z + (pRandom.nextDouble() - 0.5)) / 20
                )
            }
            if (pRandom.nextDouble() < 0.1) {
                val posDouble = pPos.toVector3d()
                pLevel.playLocalSound(
                    posDouble.x, posDouble.y, posDouble.z,
                    SoundEvents.FIRE_AMBIENT,
                    SoundSource.BLOCKS, 1.0f, 1.0f, false
                )
            }
        }
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState? =
        defaultBlockState()
            .setValue(BlockStateProperties.HORIZONTAL_FACING, pContext.horizontalDirection.opposite)
    override fun getServerTicker(pLevel: Level, pState: BlockState): BlockEntityTicker<GeneratorBlockEntity> =
        BlockEntityTicker { tLevel, pPos, tState, pBlockEntity -> pBlockEntity.tick(tLevel, pPos, tState, pBlockEntity) }
}