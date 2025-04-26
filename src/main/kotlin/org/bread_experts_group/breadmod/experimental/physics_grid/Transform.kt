package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

class Transform(
	var rotation: Quaternionf = Quaternionf(),
	var position: Vec3 = Vec3.ZERO,
	var size: Vec3 = Vec3.ZERO,
) {
	val center: Vec3
		get() = this.position.subtract(this.size.x / 2, this.size.y / 2, this.size.z / 2)
}