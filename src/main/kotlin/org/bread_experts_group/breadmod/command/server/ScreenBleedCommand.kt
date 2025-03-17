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

object ScreenBleedCommand {
	fun register(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("screenBleed")
			.requires { sourceStack -> sourceStack.hasPermission(2) }
			.then(
				Commands.argument("targets", EntityArgument.players())
					.then(this.toggle())
					.then(this.set())
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
					} else this.reset(player)
				}
				Command.SINGLE_SUCCESS
			}

	private fun reset(player: ServerPlayer) {
		screenBleedMap[player] = ScreenBleedData()
		val data = screenBleedMap[player] ?: return
		PacketDistributor.sendToPlayer(player, ScreenBleedSet(data.maxProgress))
		PacketDistributor.sendToPlayer(player, ScreenBleedToggle(active = true, reset = false))
	}

	private fun set(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("set")
			.then(
				Commands.argument("seconds", IntegerArgumentType.integer())
					.executes { ctx ->
						val amount = IntegerArgumentType.getInteger(ctx, "seconds")
						val targets = EntityArgument.getPlayers(ctx, "targets")
						targets.forEach { player ->
							PacketDistributor.sendToPlayer(player, ScreenBleedSet(amount * 20))
						}
						Command.SINGLE_SUCCESS
					}
			)
}