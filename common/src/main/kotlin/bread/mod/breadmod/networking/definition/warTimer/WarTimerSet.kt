package bread.mod.breadmod.networking.definition.warTimer

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.render.rgMinecraft
import dev.architectury.networking.NetworkManager
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal data class WarTimerSet(val time: Int) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerSet> =
            CustomPacketPayload.Type(modLocation("war_timer_set"))

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerSet> = StreamCodec.composite(
            ByteBufCodecs.INT, WarTimerSet::time
        ) { WarTimerSet(it) }

        val RECEIVER = NetworkManager.NetworkReceiver<WarTimerSet> { (time), _ ->
            WarOverlay.setTimer = 50
            WarOverlay.timeLeft = time

            val player = rgMinecraft.player ?: return@NetworkReceiver
            player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 0.8f)
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
