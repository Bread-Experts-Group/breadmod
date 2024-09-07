package bread.mod.breadmod.registry

import bread.mod.breadmod.util.render.rgMinecraft
import dev.architectury.event.events.client.ClientTickEvent

object ClientEvents {

    fun registerClientTick() = ClientTickEvent.CLIENT_POST.register { tick ->
        if (rgMinecraft.level == null || rgMinecraft.player == null) return@register

//        WarTickerClient.tick()
    }
}