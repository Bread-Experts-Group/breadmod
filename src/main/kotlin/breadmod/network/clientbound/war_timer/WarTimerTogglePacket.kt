package breadmod.network.clientbound.war_timer

import breadmod.client.gui.WarTickerClient
import net.minecraft.network.FriendlyByteBuf
import net.minecraftforge.network.NetworkEvent
import java.util.function.Supplier

class WarTimerTogglePacket(val isActive: Boolean) {
    companion object {
        fun encodeBuf(input: WarTimerTogglePacket, buffer: FriendlyByteBuf) {
            buffer.writeBoolean(input.isActive)
        }

        fun decodeBuf(input: FriendlyByteBuf): WarTimerTogglePacket =
            WarTimerTogglePacket(input.readBoolean())

        fun handle(input: WarTimerTogglePacket, ctx: Supplier<NetworkEvent.Context>) = ctx.get().let {
            it.enqueueWork {
                if (it.sender == null) {
                    WarTickerClient.active = input.isActive
                }
            }
            it.packetHandled = true
        }
    }
}