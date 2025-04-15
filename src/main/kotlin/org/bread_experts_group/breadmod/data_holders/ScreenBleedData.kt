package org.bread_experts_group.breadmod.data_holders

import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.ACTIVE
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.MAX_PROGRESS
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.PROGRESS

data class ScreenBleedData(
	var progress: Float = 0f,
	var maxProgress: Float = 169f * 20,
	var active: Boolean = true,
	var shouldOverrideDeathScreen: Boolean = false
) {
	companion object {
		/**
		 * A map holding a screen bleed timer for every player on the server.
		 */
		val screenBleedMap: MutableMap<ServerPlayer, ScreenBleedData> = mutableMapOf()
	}

	private fun isProgressMaxed(): Boolean = this.progress >= this.maxProgress

	fun tick(player: ServerPlayer) {
		if (this.active && !this.isProgressMaxed()) {
			PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(PROGRESS, number = this.progress))
			this.progress++
		} else if (this.active && this.isProgressMaxed()) {
			PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(ACTIVE))
			PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(PROGRESS))
			PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(MAX_PROGRESS))
			Companion.screenBleedMap.remove(player)
		}
	}
}