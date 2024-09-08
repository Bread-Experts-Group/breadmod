package bread.mod.breadmod.registry

import bread.mod.breadmod.command.server.WarTimerCommand
import bread.mod.breadmod.networking.definition.war_timer.WarTimerSynchronization
import bread.mod.breadmod.networking.definition.war_timer.WarTimerToggle
import bread.mod.breadmod.util.ModDamageTypes
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer

internal object CommonEvents {
    fun registerCommands() =
        CommandRegistrationEvent.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("breadmod")
                    .then(WarTimerCommand.register())
            )
        }

    /**
     * A map holding a war timer for every player on the server.
     *
     * TODO CHRIS uPDATE THIS AND ADD JAVADOC TO [WarTimerData]
     */
    val warTimerMap: MutableMap<ServerPlayer, WarTimerData> = mutableMapOf()

    data class WarTimerData(
        var timeLeft: Int = 30,
        var gracePeriod: Int = 20,
        var ticker: Int = 20,
        var increaseTime: Int = 0,
        var gracePeriodActive: Boolean = false,
        var active: Boolean = true
    )

    fun registerServerTickEvent() =
        TickEvent.Server.SERVER_POST.register {
            warTimerMap.forEach { (player, value) ->
                if (value.active && value.increaseTime == 0) {
                    if (value.ticker == 0 && value.timeLeft > 0 && !value.gracePeriodActive) {
                        value.timeLeft--
                        NetworkManager.sendToPlayer(player, WarTimerSynchronization(value.timeLeft))
                        value.ticker = 20
                    } else if (!value.gracePeriodActive && value.ticker != 0) {
                        value.ticker--
                    } else if (value.timeLeft <= 0 && !value.gracePeriodActive && value.gracePeriod != 0) {
                        value.gracePeriodActive = true
                        NetworkManager.sendToPlayer(player, WarTimerSynchronization(value.timeLeft))
                    } else if (value.gracePeriod > 0) {
                        value.gracePeriod--
                    } else if (value.timeLeft <= 0 && value.gracePeriod == 0) {
                        if (!player.isCreative) {
                            player.hurt(ModDamageTypes.TIMER_RAN_OUT.source(player.level()), Float.MAX_VALUE)
                        }
                        NetworkManager.sendToPlayer(player, WarTimerToggle(false))
                        value.gracePeriodActive = !value.gracePeriodActive
                    }
                } else if (value.increaseTime > 0 && value.active) {
                    value.increaseTime--
                    value.timeLeft++
                    value.ticker = 20
                    value.gracePeriod = 20
                    value.gracePeriodActive = false
                }
            }
        }
}