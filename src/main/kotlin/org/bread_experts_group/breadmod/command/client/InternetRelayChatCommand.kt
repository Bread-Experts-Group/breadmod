package org.bread_experts_group.breadmod.command.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.Stack

// todo FIX
internal object InternetRelayChatCommand {
	var currentSocket = Socket()
	var managingThread: Thread? = null
	val messagingStack: Stack<String> = Stack()

	private fun socketListener(ctx: CommandContext<CommandSourceStack>) {
		Thread.ofVirtual().start {
			val fqOut = this.currentSocket.outputStream
			try {
				while (true) {
					if (this.messagingStack.isEmpty()) continue
					val toSend = this.messagingStack.pop()
					ctx.source.sendSystemMessage(
						Component.literal("< " + toSend.take(toSend.length - 2)).withStyle(ChatFormatting.LIGHT_PURPLE)
					)
//					fqOut.writeString(toSend)
				}
			} catch (_: IOException) {
				this.currentSocket.close()
			}
		}

		try {
			val fqIn = this.currentSocket.inputStream
			while (true) {
//				val message = IRCMessage.read(fqIn)
//				ctx.source.sendSystemMessage(
//					Component.literal(message.toString())
//						.withStyle(ChatFormatting.YELLOW)
//				)
			}
		} catch (_: IOException) {
			ctx.source.sendSystemMessage(
				modTranslatable(
					"irc", "disconnected",
					args = listOf(this.currentSocket.remoteSocketAddress.toString())
				).withStyle(ChatFormatting.GRAY)
			)
		} catch (e: Exception) {
			ctx.source.sendFailure(
				modTranslatable(
					"irc", "connection_failure",
					args = listOf(
						this.currentSocket.remoteSocketAddress.toString(),
						e::class.simpleName ?: "General Fault",
						e.localizedMessage
					)
				)
			)
		}
		this.messagingStack.clear()
		this.currentSocket.close()
	}

	fun register(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("irc")
		.requires { sourceStack -> sourceStack.hasPermission(1) }
		.then(this.connect())
		.then(this.disconnect())
		.then(this.touch())

	private fun touch(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("transmit").then(
		Commands.argument("IRC Raw Message", StringArgumentType.string())
			.executes { ctx ->
				this.messagingStack.push(StringArgumentType.getString(ctx, "IRC Raw Message") + "\r\n")
				Command.SINGLE_SUCCESS
			}
	)

	private fun disconnect(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("disconnect").executes {
		this.managingThread?.interrupt()
		this.managingThread?.join()
		Command.SINGLE_SUCCESS
	}

	private fun connect(): ArgumentBuilder<CommandSourceStack, *> = Commands.literal("connect")
		.then(
			Commands.argument("Server Address", StringArgumentType.string())
				.then(
					Commands.argument("Server Port", IntegerArgumentType.integer(1, 65535))
						.executes { ctx ->
							this.managingThread?.interrupt()
							this.managingThread?.join()
							this.managingThread = Thread.ofVirtual().start {
								val address = InetSocketAddress(
									StringArgumentType.getString(ctx, "Server Address"),
									IntegerArgumentType.getInteger(ctx, "Server Port")
								)
								try {
									this.currentSocket = Socket()
									if (address.isUnresolved) throw UnknownHostException()
									this.currentSocket.connect(address, 3500)
									ctx.source.sendSuccess({
										modTranslatable(
											"irc", "connected",
											args = listOf(this.currentSocket.remoteSocketAddress.toString())
										).withStyle(ChatFormatting.GREEN)
									}, true)
									this.socketListener(ctx)
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
												address.address.toString(),
												IntegerArgumentType.getInteger(ctx, "Server Port")
											)
										)
									)
								} catch (e: IOException) {
									ctx.source.sendFailure(
										modTranslatable(
											"irc", "connection_failed",
											args = listOf(
												address.address.toString(),
												IntegerArgumentType.getInteger(ctx, "Server Port"),
												e::class.simpleName ?: "General Fault",
												e.localizedMessage
											)
										)
									)
								}
							}
							Command.SINGLE_SUCCESS
						}
				)
		)
}