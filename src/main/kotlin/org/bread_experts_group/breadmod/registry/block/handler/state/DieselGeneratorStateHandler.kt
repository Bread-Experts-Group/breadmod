package org.bread_experts_group.breadmod.registry.block.handler.state

import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import java.util.EnumSet

class DieselGeneratorStateHandler : GeneralStateHandler(Companion.DOOR_OPEN, Companion.UPGRADES) {
	companion object {
		val DOOR_OPEN: StateProvisioner<Boolean> = StateProvisioner(false)
		val UPGRADES: StateProvisioner<EnumSet<DieselGeneratorUpgrades>> = StateProvisioner(
			EnumSet.noneOf(DieselGeneratorUpgrades::class.java)
		)
		val BLOCK_VOID: BlockCapability<DieselGeneratorStateHandler, Void?> =
			BlockCapability.createVoid<DieselGeneratorStateHandler>(
				modLocation("diesel_generator_state_handler"),
				DieselGeneratorStateHandler::class.java
			)
	}

	enum class DieselGeneratorUpgrades {
		TURBO,
		ENERGY,
		CHARGING
	}
}