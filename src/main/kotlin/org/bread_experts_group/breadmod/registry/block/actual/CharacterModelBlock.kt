package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.DirectionProperty

class CharacterModelBlock : Block(Properties.of().noOcclusion()) {
    private companion object {
        val HORIZONTAL: DirectionProperty = BlockStateProperties.HORIZONTAL_FACING
    }

    override fun createBlockStateDefinition(pBuilder: StateDefinition.Builder<Block, BlockState>) {
        pBuilder.add(HORIZONTAL)
    }

    override fun getStateForPlacement(pContext: BlockPlaceContext): BlockState = defaultBlockState()
        .setValue(HORIZONTAL, pContext.horizontalDirection)
}