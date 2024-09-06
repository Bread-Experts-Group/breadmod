package breadmod

import breadmod.commands.server.WarTimerCommand
import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.network.PacketHandler
import breadmod.network.clientbound.war_timer.WarTimerSynchronizationPacket
import breadmod.network.clientbound.war_timer.WarTimerTogglePacket
import breadmod.util.ModDamageTypes
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
     * * Second int holds the grace period left before the timer completely expires
     * * Third int holds the delay (or ticker) before the time left decrements by one.
     * * Fourth int holds the increasing timer (how long the timer will increase for).
     * * First boolean is for if the timer grace period is active or not
     * * Second boolean is for the timer being active or not.
     * * Third boolean is if the timer is currently increasing in time.
     * * Players need to manually be added to the timer map using the warTimer command
     * * Server automatically pauses the player's timer if they leave and unpauses when they rejoin
     * * Timer is automatically removed if their time left is 0 or their timer is force ended by an admin
     */
    val warTimerMap: MutableMap<ServerPlayer, Pair<Triple<Triple<Int, Int, Boolean>, Int, Int>, Pair<Boolean, Boolean>>> = mutableMapOf()

    @SubscribeEvent
    fun serverTick(event: TickEvent.ServerTickEvent) {
        warTimerTick()
    }

    private fun warTimerTick() {
        warTimerMap.forEach { (key, value) ->
            val timeLeft = value.first.first.first
            val ticker = value.first.second
            val increaseTime = value.first.third
            val active = value.second.first
            val isIncreasing = value.second.second

            val gracePeriodActive = value.first.first.third
            val gracePeriod = value.first.first.second

            warTimerMap[key]?.let {
                if (active && !isIncreasing) {
                    if (ticker == 0 && timeLeft > 0 && !gracePeriodActive) {
                        val timeDecrement = timeLeft - 1
                        warTimerMap[key] = Triple(Triple(timeDecrement, gracePeriod, false), 40, increaseTime) to it.second
                        PacketHandler.NETWORK.send(
                            PacketDistributor.PLAYER.with { key },
                            WarTimerSynchronizationPacket(timeDecrement)
                        )
                    } else if (!gracePeriodActive && ticker != 0) {
                        val tickerDecrement = ticker - 1
                        warTimerMap[key] = Triple(Triple(timeLeft, gracePeriod, false), tickerDecrement, increaseTime) to it.second
                    } else if (timeLeft == 0 && !gracePeriodActive) {
                        warTimerMap[key] = Triple(Triple(timeLeft, gracePeriod, true), 40, increaseTime) to it.second
                        PacketHandler.NETWORK.send(
                            PacketDistributor.PLAYER.with { key },
                            WarTimerSynchronizationPacket(timeLeft)
                        )
                    } else if (gracePeriodActive && gracePeriod != 0) {
                        println(gracePeriod)
                        val gracePeriodDecrement = gracePeriod - 1
                        warTimerMap[key] = Triple(Triple(timeLeft, gracePeriodDecrement, true), 40, increaseTime) to it.second
                    } else if (timeLeft == 0) {
                        if (!key.isCreative) {
                            key.hurt(ModDamageTypes.TIMER_RAN_OUT.source(key.level()), 1000f)
                        }
                        warTimerMap.remove(key)
                        PacketHandler.NETWORK.send(
                            PacketDistributor.PLAYER.with { key },
                            WarTimerTogglePacket(false)
                        )
                    }
                }
                if (increaseTime > 0 && isIncreasing) {
                    val increasingDecrement = increaseTime - 1
                    val increasingTime = timeLeft + 1
                    warTimerMap[key] = Triple(Triple(increasingTime, gracePeriod, false), ticker, increasingDecrement) to it.second
                } else if (isIncreasing && increaseTime == 0) {
                    warTimerMap[key] = Triple(Triple(timeLeft, gracePeriod, false), ticker, increaseTime) to (it.second.first to false)
                }
            }
        }
    }

    @SubscribeEvent
    fun registerCommands(event: RegisterCommandsEvent) {
        event.dispatcher.register(Commands.literal("breadmod")
            .then(WarTimerCommand.register())
        )
    }
}