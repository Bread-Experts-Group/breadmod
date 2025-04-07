package org.bread_experts_group.breadmod.data_holders

import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus
import org.bread_experts_group.breadmod.network.clientbound.screen_bleed.ScreenBleedSynchronization
import org.bread_experts_group.breadmod.network.clientbound.screen_bleed.ScreenBleedToggle

data class ScreenBleedData(
	var progress: Int = 0,
	var maxProgress: Int = 169 * 20,
	var active: Boolean = true,
	var shouldOverrideDeathScreen: Boolean = false
) {
	fun tick(player: ServerPlayer) {
		if (this.active && this.progress <= this.maxProgress) {
			PacketDistributor.sendToPlayer(player, ScreenBleedSynchronization(this.progress))
			this.progress++
		} else if (this.active && this.progress > this.maxProgress) {
			PacketDistributor.sendToPlayer(player, ScreenBleedToggle(active = false, reset = true))
			CommonNeoForgeEventBus.screenBleedMap.remove(player)
		}
	}
}