package org.bread_experts_group.breadmod.data_holders

import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.registry.ModDamageType

data class WarTimerData(
	var timeLeft: Int = 30,
	var gracePeriod: Int = 20,
	var ticker: Int = 20,
	var increaseTime: Int = 0,
	var gracePeriodActive: Boolean = false,
	var active: Boolean = true
) {
	companion object {
		/**
		 * A map holding a war timer for every player on the server.
		 */
		val warTimerMap: MutableMap<ServerPlayer, WarTimerData> = mutableMapOf()
	}

	fun tick(player: ServerPlayer) {
		if (this.active && this.increaseTime == 0) {
			if (this.ticker == 0 && this.timeLeft > 0 && !this.gracePeriodActive) {
				this.timeLeft--
				PacketDistributor.sendToPlayer(player, WarTimerSynchronization(this.timeLeft))
				this.ticker = 20
			} else if (!this.gracePeriodActive && this.ticker != 0) {
				this.ticker--
			} else if (this.timeLeft <= 0 && !this.gracePeriodActive && this.gracePeriod != 0) {
				this.gracePeriodActive = true
				PacketDistributor.sendToPlayer(player, WarTimerSynchronization(this.timeLeft))
			} else if (this.gracePeriod > 0) {
				this.gracePeriod--
			} else if (this.timeLeft <= 0 && this.gracePeriod == 0) {
				if (!player.isCreative) {
					player.hurt(ModDamageType.TIMER_RAN_OUT.source(player.level()), Float.MAX_VALUE)
					player.level().explode(
						null,
						player.x,
						player.y,
						player.z,
						4f,
						false,
						net.minecraft.world.level.Level.ExplosionInteraction.TNT
					)
					this.active = false
					this.timeLeft = 30
				}
				PacketDistributor.sendToPlayer(player, WarTimerToggle(false))
				this.gracePeriodActive = !this.gracePeriodActive
			}
		} else if (this.increaseTime > 0 && this.active) {
			this.increaseTime--
			this.timeLeft++
			this.ticker = 20
			this.gracePeriod = 20
			this.gracePeriodActive = false
		}
	}
}