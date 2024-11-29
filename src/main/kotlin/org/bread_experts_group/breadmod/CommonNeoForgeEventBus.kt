package org.bread_experts_group.breadmod

import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.AddReloadListenerEvent
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.command.server.WarTimerCommand
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerSynchronization
import org.bread_experts_group.breadmod.network.clientbound.war_timer.WarTimerToggle
import org.bread_experts_group.breadmod.registry.ModDamageTypes

@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME)
internal object CommonNeoForgeEventBus {
    @SubscribeEvent
    fun onResourceReload(event: AddReloadListenerEvent) {
        event.addListener(ToolGunModeDataLoader)
    }

    /**
     * A map holding a war timer for every player on the server.
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

    @SubscribeEvent
    fun serverTick(event: ServerTickEvent.Post) {
        warTimerMap.forEach { (player, data) ->
            if (data.active && data.increaseTime == 0) {
                if (data.ticker == 0 && data.timeLeft > 0 && !data.gracePeriodActive) {
                    data.timeLeft--
                    PacketDistributor.sendToPlayer(player, WarTimerSynchronization(data.timeLeft))
                    data.ticker = 20
                } else if (!data.gracePeriodActive && data.ticker != 0) {
                    data.ticker--
                } else if (data.timeLeft <= 0 && !data.gracePeriodActive && data.gracePeriod != 0) {
                    data.gracePeriodActive = true
                    PacketDistributor.sendToPlayer(player, WarTimerSynchronization(data.timeLeft))
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
                            net.minecraft.world.level.Level.ExplosionInteraction.TNT
                        )
                        data.active = false
                        data.timeLeft = 30
                    }
                    PacketDistributor.sendToPlayer(player, WarTimerToggle(false))
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

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(
            Commands.literal("breadmod")
                .then(WarTimerCommand.register())
        )
    }
}