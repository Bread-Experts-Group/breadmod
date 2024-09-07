package bread.mod.breadmod.networking.definition.war_timer

import bread.mod.breadmod.ModMainCommon.modLocation
import bread.mod.breadmod.client.gui.WarOverlay
import bread.mod.breadmod.registry.sound.ModSounds
import bread.mod.breadmod.util.render.rgMinecraft
import dev.architectury.networking.NetworkManager
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload

internal data class WarTimerIncrement(val increasing: Boolean, val increaseTimer: Int) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerIncrement> =
            CustomPacketPayload.Type(modLocation("war_timer_increment"))

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerIncrement> = StreamCodec.composite(
            ByteBufCodecs.BOOL, WarTimerIncrement::increasing,
            ByteBufCodecs.INT, WarTimerIncrement::increaseTimer
        ) { increasing, timer -> WarTimerIncrement(increasing, timer) }

        val RECEIVER = NetworkManager.NetworkReceiver<WarTimerIncrement> { value, context ->
            val player = rgMinecraft.player ?: return@NetworkReceiver
            WarOverlay.isTimerIncreasing = value.increasing
            WarOverlay.increasingTimer = value.increaseTimer
            player.playSound(ModSounds.WAR_TIMER_UP.get(), 0.7f, 1.0f)
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}