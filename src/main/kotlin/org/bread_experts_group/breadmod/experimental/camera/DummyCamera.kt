package org.bread_experts_group.breadmod.experimental.camera

import net.minecraft.client.Camera

class DummyCamera : Camera() {
	public override fun setRotation(yRot: Float, xRot: Float, roll: Float) {
		super.setRotation(yRot, xRot, roll)
	}

	public override fun setPosition(x: Double, y: Double, z: Double) {
		super.setPosition(x, y, z)
	}
}