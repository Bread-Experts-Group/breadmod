package breadmod

import breadmod.commands.server.WarTimerServerCommand
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.network.PacketHandler
import breadmod.network.clientbound.war_timer.WarTimerSynchronizationPacket
import net.minecraft.commands.Commands
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.network.PacketDistributor

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object CommonForgeEventBus {
    @SubscribeEvent
    fun onResourceReload(event: AddReloadListenerEvent) {
        event.addListener(ModToolGunModeDataLoader)
    }

    /**
     * A map holding a war timer for every player on the server
     * * First int holds the time left.
     * * Second int holds the delay (or ticker) before the time left decrements by one.
     * * Third int holds the increasing timer (how long the timer will increase for).
     * * First boolean is for the timer being active or not.
     * * Second boolean is if the timer is currently increasing in time.
     * * Players need to manually be added to the timer map using the warTimer command
     * * Server automatically pauses the player's timer if they leave and unpauses when they rejoin
     * * Timer is automatically removed if their time left is 0 or their timer is force ended by an admin
     */
    val warTimerMap: MutableMap<ServerPlayer, Pair<Triple<Int, Int, Int>, Pair<Boolean, Boolean>>> = mutableMapOf()

    @SubscribeEvent
    fun serverTick(event: TickEvent.ServerTickEvent) {
        warTimerTick()
    }

    private fun warTimerTick() {
        warTimerMap.forEach { (key, value) ->
            val timeLeft = value.first.first
            val ticker = value.first.second
            val increaseTime = value.first.third
            val active = value.second.first
            val isIncreasing = value.second.second

            warTimerMap[key]?.let {
                if (active && !isIncreasing) {
                    if (ticker == 0 && timeLeft > 0) {
                        val timeDecrement = timeLeft - 1
                        warTimerMap[key] = Triple(timeDecrement, 41, increaseTime) to it.second
                        PacketHandler.NETWORK.send(
                            PacketDistributor.PLAYER.with { key },
                            WarTimerSynchronizationPacket(timeDecrement)
                        )
                    } else if (timeLeft > 0) {
                        val tickerDecrement = ticker - 1
                        warTimerMap[key] = Triple(timeLeft, tickerDecrement, increaseTime) to it.second
                    }
                }
                if (increaseTime > 0 && isIncreasing) {
                    val increasingDecrement = increaseTime - 1
                    val increasingTime = timeLeft + 1
                    warTimerMap[key] = Triple(increasingTime, ticker, increasingDecrement) to it.second
                } else if (isIncreasing && increaseTime == 0) {
                    warTimerMap[key] = Triple(timeLeft, ticker, increaseTime) to (it.second.first to false)
                }
            }
        }
    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(Commands.literal("breadmod")
            .then(WarTimerServerCommand.register())
        )
    }
}