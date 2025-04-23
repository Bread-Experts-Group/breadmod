package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.util.minus

class ClientPhysicsGrid(level: ClientLevel) : PhysicsGrid(level) {
	val renderer: PhysicsGridRenderer = PhysicsGridRenderer(this)

	init {
		check(PhysicsGridGlobals.grids[this.id] == null) { "Client Physics Grid with ID ${this.id} already exists!" }
		PhysicsGridGlobals.grids[this.id] = this
		this.logger.warn("New client sided grid created; creating render task")
		this.renderer.createRenderTask()
	}

	override fun setPos(newPos: Vec3): PhysicsGrid {
		// todo figure out interpolation to smooth this out on clientside,
		//  maybe tick the pos on client instead of sending it?
		//  Mth.lerp maybe?
		this.position -= this.position - newPos
		return this
	}
}