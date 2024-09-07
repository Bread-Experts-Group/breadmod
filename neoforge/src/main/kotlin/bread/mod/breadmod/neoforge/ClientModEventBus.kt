package bread.mod.breadmod.neoforge

import bread.mod.breadmod.ModMainCommon
import bread.mod.breadmod.client.gui.WarOverlay
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent

@Suppress("unused")
@EventBusSubscriber(modid = ModMainCommon.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientModEventBus {
    @SubscribeEvent
    fun registerOverlays(event: RegisterGuiLayersEvent) {
        event.registerAboveAll(ModMainCommon.modLocation("war_overlay"), WarOverlay())
    }
}