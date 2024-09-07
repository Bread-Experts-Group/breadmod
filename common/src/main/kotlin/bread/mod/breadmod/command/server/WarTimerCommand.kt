package bread.mod.breadmod.command.server

import bread.mod.breadmod.networking.definition.war_timer.WarTimerIncrement
import bread.mod.breadmod.networking.definition.war_timer.WarTimerToggle
import bread.mod.breadmod.registry.CommonEvents.warTimerMap
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import dev.architectury.networking.NetworkManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import kotlin.collections.set

object WarTimerCommand {
    fun register(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("warTimer")
            .then(Commands.argument("player", EntityArgument.players())
                .then(toggle())
                .then(add())
                .then(increase())
            )

    fun toggle(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("toggle")
            .executes { ctx ->
                val target = EntityArgument.getPlayers(ctx, "player")
                target.forEach { player ->
                    warTimerMap[player]?.let {
                        if (!it.second.first) {
                            warTimerMap.put(player, it.first to (true to it.second.second))
                            NetworkManager.sendToPlayer(player, WarTimerToggle(true))
                        } else {
                            warTimerMap.put(player, it.first to (false to it.second.second))
                            NetworkManager.sendToPlayer(player, WarTimerToggle(false))
                        }
                    }
                }
                return@executes Command.SINGLE_SUCCESS
            }

    fun add(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("add")
            .executes{ ctx ->
                val target = EntityArgument.getPlayers(ctx, "player")
                target.forEach { player ->
                    warTimerMap[player] = Triple(Triple(30, 20, false), 20, 0) to (false to false)
                }
                return@executes Command.SINGLE_SUCCESS
            }

    // todo figure out how to make an argument command to work alongside the player argument command
    // todo spamming this command slightly de-syncs client timer from server, corrected on next sync packet
    fun increase(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("increase")
            .executes{ ctx ->
                val target = EntityArgument.getPlayers(ctx, "player")
                target.forEach { player ->
                    warTimerMap[player]?.let {
                        val increase = it.first.third + 30
                        warTimerMap.put(player, Triple(it.first.first, 20, increase) to (it.second.first to true))
                        NetworkManager.sendToPlayer(player, WarTimerIncrement(true, increase))
                    }
                }
                return@executes Command.SINGLE_SUCCESS
            }
}