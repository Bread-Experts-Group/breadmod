package org.bread_experts_group.breadmod.command.server

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.data_holders.server.ScreenBleedData
import org.bread_experts_group.breadmod.data_holders.server.ScreenBleedData.Companion.screenBleedMap
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.ACTIVE
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.BLUE_SCREEN
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.MAX_PROGRESS
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.PROGRESS

object ScreenBleedCommand {
	fun register(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("screenBleed")
			.requires { sourceStack -> sourceStack.hasPermission(2) }
			.then(
				Commands.argument("targets", EntityArgument.players())
					.then(this.set())
					.then(this.reset())
			)

	private fun set(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("set")
			.then(this.setProgress())
			.then(this.setMaxProgress())
			.then(this.setActive())
			.then(this.setBlueScreen())

	private fun setProgress(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("progress")
			.then(
				this.seconds().executes { ctx ->
					val amount = IntegerArgumentType.getInteger(ctx, "seconds") * 20
					val targets = this.getTargets(ctx)
					targets.forEach { player ->
						val check = screenBleedMap[player]
						if (check != null) {
							check.progress = amount.toFloat()
							PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(PROGRESS, amount.toFloat()))
						} else this.init(player, startBleed = false, blueScreen = false)
					}
					Command.SINGLE_SUCCESS
				}
			)

	private fun setMaxProgress(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("maxProgress")
			.then(
				this.seconds().executes { ctx ->
					val amount = this.getSeconds(ctx)
					val targets = this.getTargets(ctx)
					targets.forEach { player ->
						val check = screenBleedMap[player]
						if (check != null) {
							check.maxProgress = amount.toFloat()
							PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(MAX_PROGRESS, amount.toFloat()))
						} else this.init(player, startBleed = false, blueScreen = false)
					}
					Command.SINGLE_SUCCESS
				}
			)

	private fun setActive(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("active")
			.then(
				this.toggle().executes { ctx ->
					val flag = this.getToggle(ctx)
					val targets = this.getTargets(ctx)
					targets.forEach { player ->
						val check = screenBleedMap[player]
						if (check != null) {
							check.active = flag
							PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(ACTIVE, bool = flag))
						} else this.init(player, startBleed = true, blueScreen = false)
					}
					Command.SINGLE_SUCCESS
				}
			)

	private fun setBlueScreen(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("blueScreen")
			.then(
				this.toggle().executes { ctx ->
					val flag = this.getToggle(ctx)
					val targets = this.getTargets(ctx)
					targets.forEach { player ->
						val check = screenBleedMap[player]
						if (check != null) {
							check.shouldOverrideDeathScreen = flag
							PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(BLUE_SCREEN, bool = flag))
						} else this.init(player, startBleed = false, blueScreen = true)
					}
					Command.SINGLE_SUCCESS
				}
			)

	private fun toggle() = Commands.argument("toggle", BoolArgumentType.bool())
	private fun getToggle(ctx: CommandContext<CommandSourceStack>) = BoolArgumentType.getBool(ctx, "toggle")

	private fun seconds() = Commands.argument("seconds", IntegerArgumentType.integer())
	private fun getSeconds(ctx: CommandContext<CommandSourceStack>) =
		IntegerArgumentType.getInteger(ctx, "seconds") * 20

	private fun getTargets(ctx: CommandContext<CommandSourceStack>) = EntityArgument.getPlayers(ctx, "targets")

	private fun reset(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("reset")
			.executes { ctx ->
				val targets = this.getTargets(ctx)
				targets.forEach { player ->
					PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(ACTIVE))
					PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(BLUE_SCREEN))
					PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(PROGRESS))
					PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(MAX_PROGRESS, number = 169f * 20))
					screenBleedMap.remove(player)
				}
				Command.SINGLE_SUCCESS
			}

	private fun init(player: ServerPlayer, startBleed: Boolean, blueScreen: Boolean) {
		screenBleedMap[player] = ScreenBleedData()
		val data = screenBleedMap[player] ?: return
		PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(PROGRESS, data.progress))
		PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(MAX_PROGRESS, data.maxProgress))
		if (startBleed) PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(ACTIVE, bool = true))
		if (blueScreen) PacketDistributor.sendToPlayer(player, ScreenBleedSetPacket(BLUE_SCREEN, bool = true))
	}
}