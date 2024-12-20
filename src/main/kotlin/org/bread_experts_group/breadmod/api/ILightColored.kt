package org.bread_experts_group.breadmod.api

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockAndTintGetter
import net.minecraft.world.level.block.state.BlockState

interface ILightColored {
	fun getLightColor(level : BlockAndTintGetter, blockState : BlockState, position : BlockPos) : Int
}