package org.bread_experts_group.breadmod.client.render

import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.toolGunModes
import org.bread_experts_group.breadmod.api.IToolGunMode
import java.math.BigDecimal
import java.security.SecureRandom

internal object ToolGunClientGlobals {
	// Miscellaneous
	internal val caseOhInstrument: SecureRandom = SecureRandom()
	internal var caseOhSize: BigDecimal = BigDecimal.TWO
	internal var currentModeIndex: Int = 0

	fun getCurrentMode(): IToolGunMode = toolGunModes.values.elementAt(this.currentModeIndex)
	fun getCurrentModeID(): ResourceLocation = toolGunModes.keys.elementAt(this.currentModeIndex)
}