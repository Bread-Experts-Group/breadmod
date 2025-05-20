package org.bread_experts_group.breadmod.command.server

import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.data_holders.server.WarTimerData
import org.bread_experts_group.breadmod.data_holders.server.WarTimerData.Companion.warTimerMap
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerIncrement
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSet
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle

internal object WarTimerCommand {
	fun register(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("warTimer")
			.requires { sourceStack -> sourceStack.hasPermission(2) }
			.then(
				Commands.argument("targets", EntityArgument.players())
					.then(this.toggle())
					.then(this.increase())
					.then(this.set())
			)

	private fun reset(player: ServerPlayer) {
		warTimerMap[player] = WarTimerData()
		PacketDistributor.sendToPlayer(player, WarTimerToggle(true))
	}

	private fun translation(endKey: String, state: Boolean, args: List<Any> = listOf()) =
		modTranslatable("command", "war_timer", endKey, if (state) "success" else "failure", args = args)

	private fun toggle(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("toggle")
			.executes { ctx ->
				val targets = EntityArgument.getPlayers(ctx, "targets")
				if (targets.isEmpty()) this.translation("toggle", false)
				targets.forEach { player ->
					val check = warTimerMap[player]
					if (check != null) {
						check.active = !check.active
						PacketDistributor.sendToPlayer(player, WarTimerToggle(check.active))
						ctx.source.sendSuccess({
							this.translation("toggle", true, listOf(check.active, player.name))
						}, true)
					} else this.reset(player)
				}
				Command.SINGLE_SUCCESS
			}

	fun increaseTime(player: ServerPlayer, data: WarTimerData, amount: Int) {
		data.increaseTime += amount
		PacketDistributor.sendToPlayer(player, WarTimerIncrement(true, data.increaseTime))
	}

	private fun setTime(player: ServerPlayer, data: WarTimerData, amount: Int) {
		data.timeLeft = amount
		data.ticker = 70
		PacketDistributor.sendToPlayer(player, WarTimerSet(amount))
	}

	private fun increaseTimeLogic(ctx: CommandContext<CommandSourceStack>, amount: Int) {
		val targets = EntityArgument.getPlayers(ctx, "targets")
		targets.forEach { player ->
			val check = warTimerMap[player]
			if (check != null) this.increaseTime(player, check, amount)
			else this.reset(player)
		}
	}

	private fun increase(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("increase")
			.executes { ctx ->
				this.increaseTimeLogic(ctx, 30)
				Command.SINGLE_SUCCESS
			}
			.then(
				Commands.argument("seconds", IntegerArgumentType.integer(1))
					.executes { ctx ->
						val amount = IntegerArgumentType.getInteger(ctx, "seconds")
						this.increaseTimeLogic(ctx, amount)
						Command.SINGLE_SUCCESS
					}
			)

	private fun set(): ArgumentBuilder<CommandSourceStack, *> =
		Commands.literal("set")
			.then(
				Commands.argument("amount", IntegerArgumentType.integer(1, 6039))
					.executes { ctx ->
						val amount = IntegerArgumentType.getInteger(ctx, "amount")
						val targets = EntityArgument.getPlayers(ctx, "targets")
						targets.forEach { player ->
							val check = warTimerMap[player]
							if (check != null) this.setTime(player, check, amount)
						}
						Command.SINGLE_SUCCESS
					}
			)
}