package bread.mod.breadmod.networking.definition.warTimer

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.gui.WarOverlay
import dev.architectury.networking.NetworkManager.NetworkReceiver
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal data class WarTimerToggle(val active: Boolean) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerToggle> =
            CustomPacketPayload.Type(modLocation("war_timer_toggle"))

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerToggle> = StreamCodec.composite(
            ByteBufCodecs.BOOL, WarTimerToggle::active
        ) { WarTimerToggle(it) }

        val RECEIVER = NetworkReceiver<WarTimerToggle> { (active), _ ->
            WarOverlay.timerActive = active
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}