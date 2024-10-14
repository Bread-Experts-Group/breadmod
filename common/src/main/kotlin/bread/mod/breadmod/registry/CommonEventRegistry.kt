package bread.mod.breadmod.registry

import bread.mod.breadmod.command.server.WarTimerCommand
import bread.mod.breadmod.entity.FakePlayer
import bread.mod.breadmod.networking.definition.ConfigValueTestPacket
import bread.mod.breadmod.networking.definition.warTimer.WarTimerSynchronization
import bread.mod.breadmod.networking.definition.warTimer.WarTimerToggle
import bread.mod.breadmod.registry.config.CommonConfig
import bread.mod.breadmod.registry.entity.ModEntityTypes
import bread.mod.breadmod.util.ModDamageTypes
import dev.architectury.event.events.common.CommandRegistrationEvent
import dev.architectury.event.events.common.PlayerEvent
import dev.architectury.event.events.common.TickEvent
import dev.architectury.networking.NetworkManager
import dev.architectury.registry.level.entity.EntityAttributeRegistry
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level

internal object CommonEventRegistry {
    // todo fix commands being broken on fabric (alternative: separate command registration for both platforms)
    fun registerCommands() =
        CommandRegistrationEvent.EVENT.register { dispatcher, context, _ ->
            dispatcher.register(
                Commands.literal("breadmod")
                    .then(WarTimerCommand.register())
            )
        }

    fun registerEntityAttributes() {
        EntityAttributeRegistry.register(ModEntityTypes.FAKE_PLAYER) { FakePlayer.createAttributes() }
    }

    fun registerPlayerConnectionEvents() {

        PlayerEvent.PLAYER_JOIN.register { serverPlayer ->
            NetworkManager.sendToPlayer(serverPlayer, ConfigValueTestPacket(CommonConfig.HAPPY_BLOCK_DIVISIONS))
        }

//        PlayerEvent.PLAYER_QUIT.register { serverPlayer ->
//
//        }
    }

    /**
     * A map holding a war timer for every player on the server.
     *
     * TODO CHRIS UPDATE THIS AND ADD JAVADOC TO [WarTimerData]
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
        TickEvent.Server.SERVER_PRE.register {
            warTimerMap.forEach { (player, data) ->
                if (data.active && data.increaseTime == 0) {
                    if (data.ticker == 0 && data.timeLeft > 0 && !data.gracePeriodActive) {
                        data.timeLeft--
                        NetworkManager.sendToPlayer(player, WarTimerSynchronization(data.timeLeft))
                        data.ticker = 20
                    } else if (!data.gracePeriodActive && data.ticker != 0) {
                        data.ticker--
                    } else if (data.timeLeft <= 0 && !data.gracePeriodActive && data.gracePeriod != 0) {
                        data.gracePeriodActive = true
                        NetworkManager.sendToPlayer(player, WarTimerSynchronization(data.timeLeft))
                    } else if (data.gracePeriod > 0) {
                        data.gracePeriod--
                    } else if (data.timeLeft <= 0 && data.gracePeriod == 0) {
                        if (!player.isCreative) {
                            player.hurt(ModDamageTypes.TIMER_RAN_OUT.source(player.level()), Float.MAX_VALUE)
                            player.level().explode(
                                null,
                                player.x,
                                player.y,
                                player.z,
                                4f,
                                false,
                                Level.ExplosionInteraction.TNT
                            )
                            data.active = false
                            data.timeLeft = 30
                        }
                        NetworkManager.sendToPlayer(player, WarTimerToggle(false))
                        data.gracePeriodActive = !data.gracePeriodActive
                    }
                } else if (data.increaseTime > 0 && data.active) {
                    data.increaseTime--
                    data.timeLeft++
                    data.ticker = 20
                    data.gracePeriod = 20
                    data.gracePeriodActive = false
                }
            }
        }
}