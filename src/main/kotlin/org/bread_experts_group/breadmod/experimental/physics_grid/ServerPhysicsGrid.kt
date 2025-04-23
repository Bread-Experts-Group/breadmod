package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.phys.AABB

class ServerPhysicsGrid(level: ServerLevel) : PhysicsGrid(level) {
	val players: MutableList<Player> = mutableListOf()

	init {
		check(PhysicsGridGlobals.grids[this.id] == null) { "Server Physics Grid with ID ${this.id} already exists!" }
		PhysicsGridGlobals.grids[this.id] = this
		this.logger.warn("New server sided grid created.")
	}

	override fun tick() {
		val aabb = AABB.ofSize(this.center, this.gridSize.x, this.gridSize.y, this.gridSize.z)
		this.players.clear()
		this.level.getEntitiesOfClass(Player::class.java, aabb.inflate(20.0)).forEach { player ->
			if (this.players.indexOf(player) == -1) this.players.add(player)
		}
		this.players.forEach { player ->
//			PacketDistributor.sendToPlayer(player as ServerPlayer, GridPosUpdatePacket(this.position, this.id + 1))
		}
//		this.setPos(this.position.plus(Vec3(0.0, 0.1, 0.0)))
	}

	override fun removeBlock(pos: BlockPos) {
		super.removeBlock(pos)
		this.syncBlockUpdate(pos)
	}

	// todo sync logic to client, get the specific client grid using this instance's id
	fun syncBlockUpdate(pos: BlockPos) {}
}