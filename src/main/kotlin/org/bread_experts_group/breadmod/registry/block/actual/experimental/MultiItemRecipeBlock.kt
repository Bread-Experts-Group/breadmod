package org.bread_experts_group.breadmod.registry.block.actual.experimental

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class MultiItemRecipeBlock : BaseEntityBlock(Properties.of()) {
    companion object {
        val CODEC = simpleCodec { MultiItemRecipeBlock() }
    }

    override fun codec(): MapCodec<out BaseEntityBlock> = CODEC

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity =
        MultiItemRecipeBlockEntity(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T>? = createTickerHelper(
        blockEntityType,
        ModBlockEntityTypes.MULTI_ITEM_TEST.get()
    ) { tLevel: Level, tPos: BlockPos, tState: BlockState, tBlockEntity: MultiItemRecipeBlockEntity ->
        tBlockEntity.tick(tLevel, tPos, tState, tBlockEntity)
    }
}