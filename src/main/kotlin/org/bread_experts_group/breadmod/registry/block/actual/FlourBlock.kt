package org.bread_experts_group.breadmod.registry.block.actual

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.FallingBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.MapColor

class FlourBlock : FallingBlock(Properties.of().ignitedByLava().mapColor(MapColor.COLOR_YELLOW).sound(SoundType.SNOW)) {
    companion object {
        val CODEC: MapCodec<out FallingBlock> = simpleCodec { FlourBlock() }
    }

    override fun isFlammable(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Boolean = true

    override fun codec(): MapCodec<out FallingBlock> = CODEC

    override fun getFlammability(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Int = 100

    override fun getFireSpreadSpeed(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        direction: Direction
    ): Int = 150
}