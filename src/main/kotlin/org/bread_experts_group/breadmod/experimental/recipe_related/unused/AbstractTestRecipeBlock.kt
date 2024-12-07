package org.bread_experts_group.breadmod.experimental.recipe_related.unused

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.bread_experts_group.breadmod.experimental.recipe_related.AbstractTestItemRecipeBlockEntity

// todo this class throws unbound value due to trying to access some things that haven't been initialized yet
@Suppress("UNCHECKED_CAST")
abstract class AbstractTestRecipeBlock<ENTITY : AbstractTestItemRecipeBlockEntity<*, *>>(
    val entityType: BlockEntityType<ENTITY>
) :
    BaseEntityBlock(Properties.of()) {
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos) as? ENTITY ?: return InteractionResult.FAIL

            player.openMenu(entity, pos)
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        blockEntityType,
        entityType
    ) { tLevel, tPos, tState, tBlockEntity ->
        tBlockEntity.tick(tLevel, tPos, tState)
    }
}