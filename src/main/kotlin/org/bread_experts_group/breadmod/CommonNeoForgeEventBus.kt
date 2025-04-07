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
import org.bread_experts_group.breadmod.BreadMod.Companion.loadToolGunModes
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.command.server.ScreenBleedCommand
import org.bread_experts_group.breadmod.command.server.WarTimerCommand
import org.bread_experts_group.breadmod.data_holders.ScreenBleedData
import org.bread_experts_group.breadmod.data_holders.WarTimerData

@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME)
internal object CommonNeoForgeEventBus {
	/**
	 * A map holding a screen bleed timer for every player on the server.
	 */
	val screenBleedMap: MutableMap<ServerPlayer, ScreenBleedData> = mutableMapOf()

	/**
	 * A map holding a war timer for every player on the server.
	 */
	val warTimerMap: MutableMap<ServerPlayer, WarTimerData> = mutableMapOf()

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