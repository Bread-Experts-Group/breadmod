package bread.mod.breadmod.command.server

import bread.mod.breadmod.networking.definition.war_timer.WarTimerIncrement
import bread.mod.breadmod.networking.definition.war_timer.WarTimerToggle
import bread.mod.breadmod.registry.CommonEvents
import bread.mod.breadmod.registry.CommonEvents.warTimerMap
import com.mojang.brigadier.Command
import com.mojang.brigadier.builder.ArgumentBuilder
import dev.architectury.networking.NetworkManager
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.server.level.ServerPlayer
import kotlin.collections.set

internal object WarTimerCommand {
    fun register(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("warTimer")
            .then(Commands.argument("player", EntityArgument.players())
                .then(toggle())
                .then(increase())
            )

    private fun reset(player: ServerPlayer) {
        warTimerMap[player] = CommonEvents.WarTimerData()
        NetworkManager.sendToPlayer(player, WarTimerToggle(true))
    }

    fun toggle(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("toggle")
            .executes { ctx ->
                val target = EntityArgument.getPlayers(ctx, "player")
                target.forEach { player ->
                    val check = warTimerMap[player]
                    if (check != null) {
                        check.active = !check.active
                        NetworkManager.sendToPlayer(player, WarTimerToggle(check.active))
                    } else reset(player)
                }
                Command.SINGLE_SUCCESS
            }

    // todo figure out how to make an argument command to work alongside the player argument command
    // todo spamming this command slightly de-syncs client timer from server, corrected on next sync packet
    fun increase(): ArgumentBuilder<CommandSourceStack, *> =
        Commands.literal("increase")
            .executes{ ctx ->
                val target = EntityArgument.getPlayers(ctx, "player")
                target.forEach { player ->
                    val check = warTimerMap[player]
                    if (check != null) {
                        check.increaseTime += 20
                        NetworkManager.sendToPlayer(player, WarTimerIncrement(true, check.increaseTime))
                    } else reset(player)
                }
                Command.SINGLE_SUCCESS
            }
}