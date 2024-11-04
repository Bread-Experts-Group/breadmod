package org.bread_experts_group.breadmod.block

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
import net.minecraft.world.level.portal.DimensionTransition
import org.bread_experts_group.breadmod.block.util.ILightningStrikeAction
import org.bread_experts_group.breadmod.registry.block.ModBlocks.LOW_DENSITY_CHARCOAL_BLOCK
import org.bread_experts_group.breadmod.registry.worldgen.dimensions.ModDimensions

class BreadBlock : FlammableBlock(
    Properties.ofFullCopy(Blocks.HAY_BLOCK)
        .strength(0.5f, 0.1f)
        .lightLevel { state -> if (state.getValue(BlockStateProperties.POWERED)) 8 else 0 }
), ILightningStrikeAction {
    init {
        this.registerDefaultState(
            this.defaultBlockState()
                .setValue(BlockStateProperties.POWERED, false)
        )
    }

    override fun isFlammable(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Boolean =
        !(state.getValue(BlockStateProperties.POWERED) || (level is ServerLevel && level.dimensionType() == ModDimensions.BREAD.first.dimensionType.second))

    override fun getFireSpreadSpeed(state: BlockState, level: BlockGetter, pos: BlockPos, direction: Direction): Int =
        120

    override fun onCaughtFire(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        direction: Direction?,
        igniter: LivingEntity?
    ) {
        level.setBlockAndUpdate(pos, LOW_DENSITY_CHARCOAL_BLOCK.get().block.defaultBlockState())
    }

    override fun entityInside(state: BlockState, level: Level, pos: BlockPos, entity: Entity) {
        if (level is ServerLevel && entity.canChangeDimensions(level, level)) {
            val resourceKey =
                if (level.dimensionType() === ModDimensions.BREAD.first.dimensionType.second) Level.OVERWORLD
                else ModDimensions.BREAD.second
            entity.changeDimension(
                DimensionTransition(
                    level.server.getLevel(resourceKey) ?: return,
                    entity.position(),
                    entity.deltaMovement,
                    entity.yRot,
                    entity.xRot,
                    DimensionTransition.DO_NOTHING
                )
            )
            level.explode(
                entity,
                pos.x.toDouble(),
                pos.y.toDouble(),
                pos.z.toDouble(),
                8.0F,
                true,
                Level.ExplosionInteraction.BLOCK
            )
        }
    }

    override fun onLightningStruck(level: Level, pos: BlockPos, state: BlockState) {
        level.playSound(null, pos, SoundEvents.WITHER_SPAWN, SoundSource.BLOCKS, 2f, 1f)
        level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.POWERED, true))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(BlockStateProperties.POWERED)
    }
}