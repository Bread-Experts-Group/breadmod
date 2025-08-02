package org.bread_experts_group.breadmod.registry.block.handler.state

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

class DoubleOrNothingStateHandler : GeneralStateHandler(
	this.DOUBLE_COUNTER, this.NOTHING, this.CASHOUT, this.JACKPOT, this.JACKPOT_TIMER,
	this.USE_NEGATIVE_TILT, this.BLOCKHEAD, this.REWIRED
) {
	companion object {
		val DOUBLE_COUNTER: StateProvisioner<Int> = StateProvisioner<Int>(0)
		val NOTHING: StateProvisioner<Boolean> = StateProvisioner<Boolean>(false)
		val CASHOUT: StateProvisioner<Boolean> = StateProvisioner<Boolean>(false)
		val JACKPOT: StateProvisioner<Boolean> = StateProvisioner<Boolean>(false)
		val JACKPOT_TIMER: StateProvisioner<Int> = StateProvisioner<Int>(0)
		val USE_NEGATIVE_TILT: StateProvisioner<Boolean> = StateProvisioner<Boolean>(false)
		val BLOCKHEAD: StateProvisioner<Boolean> = StateProvisioner<Boolean>(false)
		val REWIRED: StateProvisioner<Boolean> = StateProvisioner<Boolean>(false)
		val DATA: StateProvisioner<DoubleOrNothingData?> = StateProvisioner<DoubleOrNothingData?>(null)
		val BLOCK_VOID: BlockCapability<DoubleOrNothingStateHandler, Void?> =
			BlockCapability.createVoid<DoubleOrNothingStateHandler>(
				modLocation("double_or_nothing_state_handler"),
				DoubleOrNothingStateHandler::class.java
			)
	}

	class DoubleOrNothingData(
		val player: Player,
		val startedAt: Long,
		val wager: ItemStack = ItemStack.EMPTY
	)
}