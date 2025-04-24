package org.bread_experts_group.breadmod.command.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.irc.IRCMessage
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException

internal object InternetRelayChatCommand {
	var currentSocket = Socket()

	private fun socketListener(ctx: CommandContext<CommandSourceStack>) {
		try {
			while (true) {
				val message = IRCMessage.read(this.currentSocket.inputStream)
				ctx.source.sendSystemMessage(Component.literal(message.toString()))
			}
		} catch (e: IOException) {
			this.currentSocket.close()
			ctx.source.sendFailure(
				modTranslatable(
					"irc", "connection_failure",
					args = listOf(
						StringArgumentType.getString(ctx, "Server Address"),
						IntegerArgumentType.getInteger(ctx, "Server Port"),
						e::class.simpleName ?: "General Fault",
						e.localizedMessage
					)
				)
			)
		}
	}

	fun register(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("irc")
		.requires { sourceStack -> sourceStack.hasPermission(1) }
		.then(this.connect())

	private fun connect(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("connect")
		.then(
			Commands.argument("Server Address", StringArgumentType.string())
				.then(
					Commands.argument("Server Port", IntegerArgumentType.integer(1, 65535))
						.executes { ctx ->
							this.currentSocket.close()
							try {
								this.currentSocket = Socket()
								val address = InetSocketAddress(
									StringArgumentType.getString(ctx, "Server Address"),
									IntegerArgumentType.getInteger(ctx, "Server Port")
								)
								if (address.isUnresolved) throw UnknownHostException()
								this.currentSocket.connect(address, 3500)
								ctx.source.sendSuccess({
									modTranslatable(
										"irc", "connected",
										args = listOf(
											StringArgumentType.getString(ctx, "Server Address"),
											IntegerArgumentType.getInteger(ctx, "Server Port")
										)
									)
								}, true)
								Thread.ofVirtual().start {
									this.socketListener(ctx)
								}
							} catch (_: UnknownHostException) {
								ctx.source.sendFailure(
									modTranslatable(
										"irc", "unknown_host",
										args = listOf(
											StringArgumentType.getString(ctx, "Server Address"),
											IntegerArgumentType.getInteger(ctx, "Server Port")
										)
									)
								)
							} catch (_: SocketTimeoutException) {
								ctx.source.sendFailure(
									modTranslatable(
										"irc", "timed_out",
										args = listOf(
											StringArgumentType.getString(ctx, "Server Address"),
											IntegerArgumentType.getInteger(ctx, "Server Port")
										)
									)
								)
							} catch (e: IOException) {
								ctx.source.sendFailure(
									modTranslatable(
										"irc", "connection_failed",
										args = listOf(
											StringArgumentType.getString(ctx, "Server Address"),
											IntegerArgumentType.getInteger(ctx, "Server Port"),
											e::class.simpleName ?: "General Fault",
											e.localizedMessage
										)
									)
								)
							}
							Command.SINGLE_SUCCESS
						}
				)
		)
}