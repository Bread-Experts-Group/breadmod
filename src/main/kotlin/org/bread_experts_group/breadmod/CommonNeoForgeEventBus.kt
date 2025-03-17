package org.bread_experts_group.breadmod

import net.minecraft.commands.Commands
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod.Companion.loadToolGunModes
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.command.server.ScreenBleedCommand
import org.bread_experts_group.breadmod.command.server.WarTimerCommand
import org.bread_experts_group.breadmod.network.clientbound.screen_bleed.ScreenBleedSynchronization
import org.bread_experts_group.breadmod.network.clientbound.screen_bleed.ScreenBleedToggle
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.registry.ModDamageType

@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME)
internal object CommonNeoForgeEventBus {
	/**
	 * A map holding a screen bleed timer for every player on the server.
	 */
	val screenBleedMap: MutableMap<ServerPlayer, ScreenBleedData> = mutableMapOf()

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

	/**
	 * A map holding a war timer for every player on the server.
	 */
	val warTimerMap: MutableMap<ServerPlayer, WarTimerData> = mutableMapOf()

	data class WarTimerData(
		var timeLeft: Int = 30,
		var gracePeriod: Int = 20,
		var ticker: Int = 20,
		var increaseTime: Int = 0,
		var gracePeriodActive: Boolean = false,
		var active: Boolean = true
	) {
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

	@SubscribeEvent
	fun onPlayerLoginServer(event: PlayerLoggedInEvent) {
		println("server player login success")
	}

	@SubscribeEvent
	fun serverTick(event: ServerTickEvent.Post) {
		this.warTimerMap.forEach { (player, data) -> data.tick(player) }
		this.screenBleedMap.forEach { (player, data) -> data.tick(player) }
	}

	val toolGunModes: MutableMap<ResourceLocation, IToolGunMode> = mutableMapOf()

	@SubscribeEvent
	fun onServerStarted(event: ServerStartedEvent) {
		loadToolGunModes()
	}

	@SubscribeEvent
	fun registerCommands(event: RegisterCommandsEvent) {
		event.dispatcher.register(
			Commands.literal(BreadMod.ID)
				.then(WarTimerCommand.register())
				.then(ScreenBleedCommand.register())
		)
	}
}