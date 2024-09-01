package breadmod.network.clientbound.war_timer

import breadmod.client.gui.WarTickerClient
import breadmod.registry.sound.ModSounds
import breadmod.util.render.rgMinecraft
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class WarTimerSynchronizationPacket(val timeLeft: Int) {
    companion object {
        fun encodeBuf(input: WarTimerSynchronizationPacket, buffer: FriendlyByteBuf) {
            buffer.writeVarInt(input.timeLeft)
        }

        fun decodeBuf(input: FriendlyByteBuf): WarTimerSynchronizationPacket =
            WarTimerSynchronizationPacket(input.readVarInt())

        fun handle(input: WarTimerSynchronizationPacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                if (it.sender == null) {
                    val player = rgMinecraft.player ?: return@enqueueWork
                    WarTickerClient.timer = input.timeLeft
                    player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 1f)
                }
            }
            it.packetHandled = true
        }
    }
}