package org.bread_experts_group.breadmod.client.render.tool_gun

import org.bread_experts_group.breadmod.client.render.localClient

// todo convert this to use millis and merge into the renderer (look in WarOverlay for reference)
internal object ToolGunAnimationHandler {
	var coilRotation: Float = 0f
	private var coilDelta: Float = 0f
	var recoil: Float = 0f
	fun clientTick() {
		if (!localClient.isPaused) {
			this.coilRotation += 2f * this.coilDelta
			if (this.coilDelta > 0f) this.coilDelta -= 0.1f * localClient.timer.realtimeDeltaTicks / 1.05f
			else this.coilDelta = 0f
			if (this.recoil > 0f) this.recoil -= 0.05f * (localClient.timer.realtimeDeltaTicks / 10)
			else this.recoil = 0f
			this.coilRotation %= 360
		}
	}
//    fun trigger() {
//        coilDelta = 4f + random.nextFloat()
//        recoil = 0.1f
//    }
}