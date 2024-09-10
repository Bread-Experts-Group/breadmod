package bread.mod.breadmod.networking

import bread.mod.breadmod.networking.definition.warTimer.WarTimerIncrement
import bread.mod.breadmod.networking.definition.warTimer.WarTimerSynchronization
import bread.mod.breadmod.networking.definition.warTimer.WarTimerToggle
import dev.architectury.networking.NetworkManager
import dev.architectury.networking.NetworkManager.NetworkReceiver
import dev.architectury.platform.Platform
import dev.architectury.utils.Env
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal object Networking {
    private fun <T : CustomPacketPayload> registerS2CWithReceiver(
        type: CustomPacketPayload.Type<T>,
        codec: StreamCodec<ByteBuf, T>,
        receiver: NetworkReceiver<T>
    ) {
        if (Platform.getEnvironment() == Env.SERVER) NetworkManager.registerS2CPayloadType(type, codec)
        else NetworkManager.registerReceiver(
            NetworkManager.s2c(),
            type, codec,
            receiver
        )
    }

    fun registerNetworking() {
        registerS2CWithReceiver(
            WarTimerSynchronization.TYPE, WarTimerSynchronization.STREAM_CODEC,
            WarTimerSynchronization.RECEIVER
        )
        registerS2CWithReceiver(
            WarTimerIncrement.TYPE, WarTimerIncrement.STREAM_CODEC,
            WarTimerIncrement.RECEIVER
        )
        registerS2CWithReceiver(
            WarTimerToggle.TYPE, WarTimerToggle.STREAM_CODEC,
            WarTimerToggle.RECEIVER
        )
    }
}