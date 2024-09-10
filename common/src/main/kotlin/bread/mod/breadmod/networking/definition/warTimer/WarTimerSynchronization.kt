package bread.mod.breadmod.networking.definition.warTimer

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.render.rgMinecraft
import dev.architectury.networking.NetworkManager.NetworkReceiver
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal data class WarTimerSynchronization(val time: Int) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerSynchronization> =
            CustomPacketPayload.Type(modLocation("war_timer_sync"))

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerSynchronization> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            WarTimerSynchronization::time
        ) { WarTimerSynchronization(it) }

        val RECEIVER = NetworkReceiver<WarTimerSynchronization> { (time), _ ->
            WarOverlay.timeLeft = time

            val player = rgMinecraft.player ?: return@NetworkReceiver
            player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 1f)
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}