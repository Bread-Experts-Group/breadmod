package org.bread_experts_group.breadmod.experimental.physics_grid.dummy_level

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.ticks.LevelTickAccess
import net.minecraft.world.ticks.ScheduledTick
import net.minecraft.world.ticks.TickContainerAccess

object DummyTickAccess {
	object LBlock : LevelTickAccess<Block> {
		override fun schedule(tick: ScheduledTick<Block>) {}
		override fun hasScheduledTick(pos: BlockPos, type: Block): Boolean = false
		override fun count(): Int = 0
		override fun willTickThisTick(pos: BlockPos, type: Block): Boolean = false
	}

	object TBlock : TickContainerAccess<Block> {
		override fun schedule(tick: ScheduledTick<Block>) {}
		override fun hasScheduledTick(pos: BlockPos, type: Block): Boolean = false
		override fun count(): Int = 0
	}

	object LFluid : LevelTickAccess<Fluid> {
		override fun schedule(tick: ScheduledTick<Fluid>) {}
		override fun hasScheduledTick(pos: BlockPos, type: Fluid): Boolean = false
		override fun count(): Int = 0
		override fun willTickThisTick(pos: BlockPos, type: Fluid): Boolean = false
	}

	object TFluid : TickContainerAccess<Fluid> {
		override fun schedule(tick: ScheduledTick<Fluid>) {}
		override fun hasScheduledTick(pos: BlockPos, type: Fluid): Boolean = false
		override fun count(): Int = 0
	}
}