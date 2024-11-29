package org.bread_experts_group.breadmod

import net.minecraft.server.level.ServerPlayer
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader

@Suppress("unused")
@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.DEDICATED_SERVER])
internal object DedicatedServerNeoForgeEventBus {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerLoggedInEvent) {
        val list =
            buildList { ToolGunModeDataLoader.modes.forEach { it.value.forEach { p -> add(p.value) } } }.iterator()
        while (list.hasNext()) {
            val next = list.next()
            PacketDistributor.sendToPlayer(
                event.entity as ServerPlayer,
                ToolGunModeDataPacket(next.second, next.third, list.hasNext())
            )
        }
    }
}