package org.bread_experts_group.breadmod.client.tool_gun.render

import java.math.BigDecimal
import java.security.SecureRandom

internal object ToolGunClientGlobals {
	// Miscellaneous
	internal val caseOhInstrument: SecureRandom = SecureRandom()
	internal var caseOhSize: BigDecimal = BigDecimal.TWO
	internal var currentModeIndex: Int = 0

	// Recoil and Coil Spin vars
	internal var coilRotation: Float = 0f
	internal var coilDelta: Float = 0f
	internal var recoil: Float = 0f
	// Operations
	/**
	 * Sets the delta and recoil to their triggered values.
	 */
	fun triggerDelta() {
		this.coilDelta = 1f
		this.recoil = 0.1f
	}
}