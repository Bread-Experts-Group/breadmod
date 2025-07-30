package org.bread_experts_group.breadmod.registry.block.actual.entity.handler.state

import net.minecraft.core.BlockPos
import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class KeyboardStateHandler : GeneralStateHandler() {
	companion object {
		val MONITOR_POSITION: StateProvisioner<BlockPos?> = StateProvisioner<BlockPos?>(null)
		val BLOCK_VOID: BlockCapability<KeyboardStateHandler, Void?> =
			BlockCapability.createVoid<KeyboardStateHandler>(
				modLocation("keyboard_state_handler"),
				KeyboardStateHandler::class.java
			)
	}
}