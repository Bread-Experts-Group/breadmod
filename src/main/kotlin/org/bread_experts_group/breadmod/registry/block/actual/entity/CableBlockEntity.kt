package org.bread_experts_group.breadmod.registry.block.actual.entity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadmod.registry.block.ModBlockEntityTypes

class CableBlockEntity(
	pos: BlockPos,
	state: BlockState
) : BreadModBlockEntity<CableBlockEntity>(ModBlockEntityTypes.CABLE.get(), pos, state) {
	val connectedSides: MutableList<Direction> = mutableListOf()

	fun updateSide(side: Direction, add: Boolean) {
		if (add) this.connectedSides.add(side) else this.connectedSides.remove(side)
	}
}