package breadmod.commands.server

import breadmod.CommonForgeEventBus.warTimerMap
import breadmod.network.PacketHandler
import breadmod.network.clientbound.war_timer.WarTimerIncrementPacket
import breadmod.network.clientbound.war_timer.WarTimerTogglePacket
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraftforge.network.PacketDistributor

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
                            PacketHandler.NETWORK.send(
                                PacketDistributor.PLAYER.with { player },
                                WarTimerTogglePacket(true)
                            )
                        } else {
                            warTimerMap.put(player, it.first to (false to it.second.second))
                            PacketHandler.NETWORK.send(
                                PacketDistributor.PLAYER.with { player },
                                WarTimerTogglePacket(false)
                            )
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
                    warTimerMap[player] = Triple(Triple(30, 40, false), 40, 0) to (false to false)
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
                        warTimerMap.put(player, Triple(it.first.first, 40, increase) to (it.second.first to true))
                        PacketHandler.NETWORK.send(
                            PacketDistributor.PLAYER.with { player },
                            WarTimerIncrementPacket(true, increase)
                        )
                    }
                }
                return@executes Command.SINGLE_SUCCESS
            }

//                    this throws invalid player data on join
//                    .then(Commands.argument("increase", CommandArguments.IntArgument())
//                        .executes { ctx ->
//                            val target = EntityArgument.getPlayer(ctx, "player")
//                            val argument = ctx.getArgument("increase", Int::class.java)
//                            println("$argument")
//                            return@executes Command.SINGLE_SUCCESS
//                        }
//                    )
}