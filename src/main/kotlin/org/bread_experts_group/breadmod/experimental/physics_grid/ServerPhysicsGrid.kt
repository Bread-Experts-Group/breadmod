package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player

class ServerPhysicsGrid(
	level: ServerLevel,
	posA: BlockPos,
	posB: BlockPos
) : PhysicsGrid(level, posA, posB) {
	val players: MutableList<Player> = mutableListOf()

	init {
		check(PhysicsGridGlobals.grids[this.id] == null) { "Server Physics Grid with ID ${this.id} already exists!" }
		PhysicsGridGlobals.grids[this.id] = this
		this.logger.warn("New server sided grid created.")
	}

	// todo sync logic to client, get the specific client grid using this instance's id
	fun syncBlockUpdate(pos: BlockPos) {}
}