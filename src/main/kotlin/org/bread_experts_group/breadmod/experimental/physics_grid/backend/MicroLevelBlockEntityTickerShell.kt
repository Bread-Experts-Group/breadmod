package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.TickingBlockEntity

class MicroLevelBlockEntityTickerShell(var ticker: TickingBlockEntity) : TickingBlockEntity {
	override fun tick(): Unit = this.ticker.tick()
	override fun isRemoved(): Boolean = this.ticker.isRemoved
	override fun getType(): String = this.ticker.type
	override fun getPos(): BlockPos = this.ticker.pos
}