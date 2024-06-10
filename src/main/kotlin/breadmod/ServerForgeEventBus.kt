package breadmod

import breadmod.datagen.tool_gun.ModToolGunModeDataLoader
import breadmod.network.PacketHandler.NETWORK
import breadmod.network.tool_gun.ToolGunModeDataPacket
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.network.PacketDistributor

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = [Dist.DEDICATED_SERVER])
object ServerForgeEventBus {
    @SubscribeEvent
    fun onPlayerJoin(event: PlayerLoggedInEvent) {
        val list = buildList { ModToolGunModeDataLoader.modes.forEach { it.value.forEach { p -> add(p.value) } } }.iterator()
        while(list.hasNext()) {
            val next = list.next()
            NETWORK.send(
                PacketDistributor.PLAYER.with { event.entity as ServerPlayer },
                ToolGunModeDataPacket(next.second, next.third, list.hasNext())
            )
        }
    }
}