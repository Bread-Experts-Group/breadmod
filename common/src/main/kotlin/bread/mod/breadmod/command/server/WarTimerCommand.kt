package bread.mod.breadmod.command.server

import bread.mod.breadmod.networking.definition.warTimer.WarTimerIncrement
import bread.mod.breadmod.networking.definition.warTimer.WarTimerSet
import bread.mod.breadmod.networking.definition.warTimer.WarTimerToggle
import bread.mod.breadmod.registry.CommonEventRegistry
import bread.mod.breadmod.registry.CommonEventRegistry.warTimerMap
import com.mojang.brigadier.Command
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.ArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import dev.architectury.networking.NetworkManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import kotlin.collections.set

internal object WarTimerCommand {
    fun register(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("warTimer")
            .requires { sourceStack -> sourceStack.hasPermission(2) }
            .then(
                Commands.argument("targets", EntityArgument.players())
                    .then(toggle())
                    .then(increase())
                    .then(set())
            )

    private fun reset(player: ServerPlayer) {
        warTimerMap[player] = CommonEventRegistry.WarTimerData()
        NetworkManager.sendToPlayer(player, WarTimerToggle(true))
    }

    private fun toggle(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("toggle")
            .executes { ctx ->
                val targets = EntityArgument.getPlayers(ctx, "targets")
                targets.forEach { player ->
                    val check = warTimerMap[player]
                    if (check != null) {
                        check.active = !check.active
                        NetworkManager.sendToPlayer(player, WarTimerToggle(check.active))
                    } else reset(player)
                }
                Command.SINGLE_SUCCESS
            }

    internal fun increaseTime(player: ServerPlayer, data: CommonEventRegistry.WarTimerData, amount: Int) {
        data.increaseTime += amount
        NetworkManager.sendToPlayer(player, WarTimerIncrement(true, data.increaseTime))
    }

    internal fun setTime(player: ServerPlayer, data: CommonEventRegistry.WarTimerData, amount: Int) {
        data.timeLeft = amount
        data.ticker = 70
        NetworkManager.sendToPlayer(player, WarTimerSet(amount))
    }

    private fun increaseTimeLogic(ctx: CommandContext<CommandSourceStack>, amount: Int) {
        val targets = EntityArgument.getPlayers(ctx, "targets")
        targets.forEach { player ->
            val check = warTimerMap[player]
            if (check != null) increaseTime(player, check, amount)
            else reset(player)
        }
    }

    private fun increase(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("increase")
            .executes { ctx ->
                increaseTimeLogic(ctx, 30)
                Command.SINGLE_SUCCESS
            }
            .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                .executes { ctx ->
                    val amount = IntegerArgumentType.getInteger(ctx, "amount")
                    increaseTimeLogic(ctx, amount)
                    Command.SINGLE_SUCCESS
                }
            )

    private fun set(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("set")
            .then(Commands.argument("amount", IntegerArgumentType.integer(1, 6039))
                .executes { ctx ->
                    val amount = IntegerArgumentType.getInteger(ctx, "amount")
                    val targets = EntityArgument.getPlayers(ctx, "targets")
                    targets.forEach { player ->
                        val check = warTimerMap[player]
                        if (check != null) setTime(player, check, amount)
                    }
                    Command.SINGLE_SUCCESS
                }
            )
}