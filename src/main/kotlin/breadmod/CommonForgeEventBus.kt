package breadmod

import breadmod.datagen.toolgun.ModToolgunModeDataLoader
import net.minecraftforge.event.AddReloadListenerEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod.EventBusSubscriber(modid = ModMain.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object CommonForgeEventBus {
    @SubscribeEvent
    fun onResourceReload(event: AddReloadListenerEvent) {
        event.addListener(ModToolgunModeDataLoader)
    }
}