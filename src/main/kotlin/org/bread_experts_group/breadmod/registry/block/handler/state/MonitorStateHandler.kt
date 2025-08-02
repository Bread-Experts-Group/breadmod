package org.bread_experts_group.breadmod.registry.block.handler.state

import net.minecraft.core.BlockPos
import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class MonitorStateHandler : GeneralStateHandler() {
	companion object {
		val KEYBOARD_POSITION: StateProvisioner<BlockPos?> = StateProvisioner<BlockPos?>(null)
		val BLOCK_VOID: BlockCapability<MonitorStateHandler, Void?> =
			BlockCapability.createVoid<MonitorStateHandler>(
				modLocation("monitor_state_handler"),
				MonitorStateHandler::class.java
			)
	}
}