package org.bread_experts_group.breadmod.registry.block.actual

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState

open class FlammableBlock(properties: Properties) : BreadModBlock(properties.ignitedByLava()) {
	override fun isFlammable(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		direction: Direction
	): Boolean = true

	override fun getFlammability(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		direction: Direction
	): Int = 15

	override fun getFireSpreadSpeed(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		direction: Direction
	): Int = 30
}