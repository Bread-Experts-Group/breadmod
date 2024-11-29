package org.bread_experts_group.breadmod

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.common.EventBusSubscriber

@Suppress("unused")
@EventBusSubscriber(modid = BreadMod.ID, bus = EventBusSubscriber.Bus.GAME, value = [Dist.DEDICATED_SERVER])
internal object DedicatedServerNeoForgeEventBus {
//    @SubscribeEvent
//    fun onPlayerJoin(event: PlayerLoggedInEvent) {
//        val list =
//            buildList { ToolGunModeDataLoader.modes.forEach { it.value.forEach { p -> add(p.value) } } }.iterator()
//        while (list.hasNext()) {
//            val next = list.next()
//            PacketDistributor.sendToPlayer(
//                event.entity as ServerPlayer,
//                ToolGunModeDataPacket(next.second, next.third, list.hasNext())
//            )
//        }
//    }
}