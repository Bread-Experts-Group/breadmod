package org.bread_experts_group.breadmod

import net.minecraft.commands.Commands
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.server.ServerStartedEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import org.bread_experts_group.breadmod.BreadMod.Companion.loadToolGunModes
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.command.server.ScreenBleedCommand
import org.bread_experts_group.breadmod.command.server.WarTimerCommand
import org.bread_experts_group.breadmod.data_holders.ScreenBleedData.Companion.screenBleedMap
import org.bread_experts_group.breadmod.data_holders.WarTimerData.Companion.warTimerMap
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGridGlobals

@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME)
internal object CommonNeoForgeEventBus {
	//	@SubscribeEvent
//	fun onPlayerLoginServer(event: PlayerLoggedInEvent) {
//		println("server player login success")
//	}
	@SubscribeEvent
	fun serverTick(event: ServerTickEvent.Post) {
		warTimerMap.forEach { (player, data) -> data.tick(player) }
		screenBleedMap.forEach { (player, data) -> data.tick(player) }
		PhysicsGridGlobals.grids.values.forEach(PhysicsGrid::tick)
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