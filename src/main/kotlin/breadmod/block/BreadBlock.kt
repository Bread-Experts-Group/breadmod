package breadmod.block

import breadmod.registry.worldgen.dimensions.ModDimensions
import breadmod.registry.block.ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

@ISpecialFireAction
class BreadBlock : FlammableBlock(
    Properties.copy(Blocks.HAY_BLOCK)
        .strength(0.5f)
        .lightLevel { state -> if(state.getValue(BlockStateProperties.POWERED)) 8 else 0 }
), ILightningStrikeAction {
    init {
        this.registerDefaultState(this.defaultBlockState()
            .setValue(BlockStateProperties.POWERED, false)
        )
    }

    override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean =
        !(state.getValue(BlockStateProperties.POWERED) || (level is ServerLevel && level.dimensionType() == ModDimensions.BREAD.first.dimensionType.second))
    override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int
            = 120

    override fun onCaughtFire(
        state: BlockState,
        pLevel: Level,
        pPos: BlockPos,
        direction: Direction?,
        igniter: LivingEntity?,
    ) {
        pLevel.setBlockAndUpdate(pPos, LOW_DENSITY_CHARCOAL_BLOCK.get().block.defaultBlockState())
    }

    @Deprecated("Deprecated in Java", ReplaceWith("super.entityInside(pState, pLevel, pPos, pEntity)"))
    override fun entityInside(pState: BlockState, pLevel: Level, pPos: BlockPos, pEntity: Entity) {
        if (pLevel is ServerLevel && pEntity.canChangeDimensions()) {
            val resourceKey =
                if (pLevel.dimensionType() === ModDimensions.BREAD.first.dimensionType.second) Level.OVERWORLD
                else ModDimensions.BREAD.second
            pEntity.changeDimension(pLevel.server.getLevel(resourceKey) ?: return)
            pLevel.explode(pEntity, pPos.x.toDouble(), pPos.y.toDouble(), pPos.z.toDouble(), 8.0F, true, Level.ExplosionInteraction.BLOCK)
        }
    }

    override fun onLightningStruck(pLevel: Level, pPos: BlockPos, pState: BlockState) {
        pLevel.playSound(null, pPos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 2.0F, 1.0F)
        pLevel.setBlockAndUpdate(pPos, pState.setValue(BlockStateProperties.POWERED, true))
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(BlockStateProperties.POWERED)
    }
}