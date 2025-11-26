package org.bread_experts_group.breadmod.experimental.camera_viewer

import net.minecraft.client.Camera
import net.minecraft.world.entity.Display
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class DummyCamera : Camera() {
	fun setEntity(level: Level) {
		if (this.entity != null) return
		this.entity = Display.BlockDisplay(EntityType.BLOCK_DISPLAY, level)
	}

	public override fun setRotation(yRot: Float, xRot: Float, roll: Float) {
		super.setRotation(yRot, xRot, roll)
	}

	public override fun setPosition(x: Double, y: Double, z: Double) {
		super.setPosition(x, y, z)
	}

	@Deprecated("Deprecated in Java")
	public override fun setRotation(xRot: Float, yRot: Float) {
		super.setRotation(xRot, yRot)
	}
}