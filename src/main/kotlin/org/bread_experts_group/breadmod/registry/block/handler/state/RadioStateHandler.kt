package org.bread_experts_group.breadmod.registry.block.handler.state

import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class RadioStateHandler : GeneralStateHandler(
	this.DISPLAY_FLIP
) {
	companion object {
		val DISPLAY_FLIP: StateProvisioner<Float> = StateProvisioner<Float>(0f)
		val BLOCK_VOID: BlockCapability<RadioStateHandler, Void?> =
			BlockCapability.createVoid<RadioStateHandler>(
				modLocation("radio_state_handler"),
				RadioStateHandler::class.java
			)
	}
}