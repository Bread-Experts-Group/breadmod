package breadmod

import breadmod.capabilities.UltimateBreadItemCapabilityProvider
import net.minecraft.world.entity.Entity
import net.minecraftforge.event.AttachCapabilitiesEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod

@Suppress("unused")
@Mod.EventBusSubscriber(modid = BreadMod.ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
object ForgeEventBus {
    @SubscribeEvent
    fun attachCapabilitiesEntity(event: AttachCapabilitiesEvent<Entity>) {
        event.addCapability(BreadMod.modLocation("used_ultimate_bread"), UltimateBreadItemCapabilityProvider())
    }

    private var ticks = 0
    val serverEvents = mutableMapOf<Int, () -> Unit>()
    fun scheduleServerEvent(inTicks: Int, event: () -> Unit) = (ticks + inTicks).also { serverEvents[it] = event }

    @SubscribeEvent
    fun serverTicker(event: TickEvent.ServerTickEvent) {
        serverEvents[ticks]?.invoke()
        ticks++
    }
}