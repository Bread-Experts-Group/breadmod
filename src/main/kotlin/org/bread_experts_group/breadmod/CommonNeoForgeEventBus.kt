package org.bread_experts_group.breadmod

import net.minecraft.commands.Commands
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.api.ToolGunMode
import org.bread_experts_group.breadmod.command.server.WarTimerCommand
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.registry.ModDamageType
import org.bread_experts_group.breadmod.util.reflect.LibraryScanner
import kotlin.reflect.full.primaryConstructor

@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME)
internal object CommonNeoForgeEventBus {
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
	)

	@SubscribeEvent
	fun serverTick(@Suppress("unused") event: ServerTickEvent.Post) {
		this.warTimerMap.forEach { (player, data) ->
			if (data.active && data.increaseTime == 0) {
				if (data.ticker == 0 && data.timeLeft > 0 && !data.gracePeriodActive) {
					data.timeLeft--
					PacketDistributor.sendToPlayer(player, WarTimerSynchronization(data.timeLeft))
					data.ticker = 20
				} else if (!data.gracePeriodActive && data.ticker != 0) {
					data.ticker--
				} else if (data.timeLeft <= 0 && !data.gracePeriodActive && data.gracePeriod != 0) {
					data.gracePeriodActive = true
					PacketDistributor.sendToPlayer(player, WarTimerSynchronization(data.timeLeft))
				} else if (data.gracePeriod > 0) {
					data.gracePeriod--
				} else if (data.timeLeft <= 0 && data.gracePeriod == 0) {
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
						data.active = false
						data.timeLeft = 30
					}
					PacketDistributor.sendToPlayer(player, WarTimerToggle(false))
					data.gracePeriodActive = !data.gracePeriodActive
				}
			} else if (data.increaseTime > 0 && data.active) {
				data.increaseTime--
				data.timeLeft++
				data.ticker = 20
				data.gracePeriod = 20
				data.gracePeriodActive = false
			}
		}
	}

	val toolGunModes: MutableMap<ResourceLocation, IToolGunMode> = mutableMapOf()

	@SubscribeEvent
	fun onServerStarted(event: ServerStartedEvent) {
		val libraryScanner = LibraryScanner(BreadMod::class.java.classLoader, BreadMod::class.java.`package`)
		libraryScanner.getClassesAnnotatedWith<ToolGunMode>().forEach {
			val mode = (it.primaryConstructor ?: return@forEach).call() as IToolGunMode
			this.toolGunModes[mode.getUid()] = mode
		}
	}

	@SubscribeEvent
	fun registerCommands(event: RegisterCommandsEvent) {
		event.dispatcher.register(
			Commands.literal(BreadMod.ID)
				.then(WarTimerCommand.register())
		)
	}
}