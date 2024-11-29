package org.bread_experts_group.breadmod.experimental.block.single_item

import com.mojang.serialization.MapCodec
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
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class SingleItemRecipeBlock : BaseEntityBlock(Properties.of()) {
    companion object {
        val CODEC = simpleCodec { SingleItemRecipeBlock() }
    }

    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult
    ): InteractionResult {
        if (!level.isClientSide) {
            val entity = level.getBlockEntity(pos) as? SingleItemRecipeBlockEntity ?: return InteractionResult.FAIL
            player.openMenu(entity, pos)
        }
        return InteractionResult.sidedSuccess(level.isClientSide)
    }

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        SingleItemRecipeBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        blockEntityType,
        ModBlockEntityTypes.SINGLE_ITEM_TEST.get()
    ) { tLevel, tPos, tState, tBlockEntity ->
        tBlockEntity.tick(tLevel, tPos, tState)
    }
}