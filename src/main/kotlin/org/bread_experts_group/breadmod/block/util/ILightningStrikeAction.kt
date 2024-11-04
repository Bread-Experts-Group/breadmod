package org.bread_experts_group.breadmod.block.util

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

interface ILightningStrikeAction {
    fun onLightningStruck(level: Level, pos: BlockPos, state: BlockState)
}