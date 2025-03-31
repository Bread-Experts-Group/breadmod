package org.bread_experts_group.breadmod.command.server

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.ScreenBleedData
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus.screenBleedMap
import org.bread_experts_group.breadmod.network.clientbound.screen_bleed.ScreenBleedSet
import org.bread_experts_group.breadmod.network.clientbound.screen_bleed.ScreenBleedToggle

// todo bugs
//  setting the timer after the screen bleed already completed does not re-toggle the effect until toggle is ran again
//  effect moving past it's intended end point on the bottom of the screen
object ScreenBleedCommand {
	fun register(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("screenBleed")
			.requires { sourceStack -> sourceStack.hasPermission(2) }
			.then(
				Commands.argument("targets", EntityArgument.players())
					.then(this.toggle())
					.then(this.set())
					.then(this.reset())
			)

	private fun toggle(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("toggle")
			.executes { ctx ->
				val targets = EntityArgument.getPlayers(ctx, "targets")
				targets.forEach { player ->
					val check = screenBleedMap[player]
					if (check != null) {
						check.active = !check.active
						PacketDistributor.sendToPlayer(player, ScreenBleedToggle(check.active, false))
					} else this.init(player, true)
				}
				Command.SINGLE_SUCCESS
			}

	private fun reset(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("reset")
			.executes { ctx ->
				val targets = EntityArgument.getPlayers(ctx, "targets")
				targets.forEach { player ->
					PacketDistributor.sendToPlayer(player, ScreenBleedToggle(active = false, reset = true))
					screenBleedMap.remove(player)
				}
				Command.SINGLE_SUCCESS
			}

	private fun init(player: ServerPlayer, startBleed: Boolean) {
		screenBleedMap[player] = ScreenBleedData()
		val data = screenBleedMap[player] ?: return
		PacketDistributor.sendToPlayer(player, ScreenBleedSet(data.progress, data.maxProgress))
		PacketDistributor.sendToPlayer(player, ScreenBleedToggle(active = startBleed, reset = false))
	}

	private fun set(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("set")
			.then(
				Commands.argument("seconds", IntegerArgumentType.integer())
					.executes { ctx ->
						val amount = IntegerArgumentType.getInteger(ctx, "seconds")
						val targets = EntityArgument.getPlayers(ctx, "targets")
						targets.forEach { player ->
							val check = screenBleedMap[player]
							if (check != null) {
								PacketDistributor.sendToPlayer(player, ScreenBleedSet(0, amount * 20))
							} else this.init(player, false)
						}
						Command.SINGLE_SUCCESS
					}
			)
}