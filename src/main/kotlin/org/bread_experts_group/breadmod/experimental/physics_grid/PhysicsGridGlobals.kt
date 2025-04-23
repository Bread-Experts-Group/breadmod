package org.bread_experts_group.breadmod.experimental.physics_grid

object PhysicsGridGlobals {
	val serverGrids: MutableMap<Int, ServerPhysicsGrid> = mutableMapOf()
	val clientGrids: MutableMap<Int, ClientPhysicsGrid> = mutableMapOf()
	var idCounter: Int = 0
}