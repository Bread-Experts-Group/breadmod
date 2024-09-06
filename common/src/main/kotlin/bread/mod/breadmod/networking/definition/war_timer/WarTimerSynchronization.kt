package bread.mod.breadmod.networking.definition.war_timer

import bread.mod.breadmod.ModMainCommon
import dev.architectury.networking.NetworkManager.NetworkReceiver
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal data class WarTimerSynchronization(val time: Int?) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerSynchronization> =
            CustomPacketPayload.createType(ModMainCommon.MOD_ID + ":war_timer_sync")

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerSynchronization> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WarTimerSynchronization::time
        ) { WarTimerSynchronization(it) }

        val RECEIVER = NetworkReceiver<WarTimerSynchronization> { value, context ->
            println("Packet received, ${value.time}, ${context.player}")
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}