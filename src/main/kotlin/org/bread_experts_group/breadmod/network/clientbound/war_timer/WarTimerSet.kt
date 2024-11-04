package org.bread_experts_group.breadmod.network.clientbound.war_timer

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.Breadmod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.WarOverlay
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.util.rgMinecraft

internal data class WarTimerSet(val time: Int) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerSet> =
            CustomPacketPayload.Type(modLocation("war_timer_set"))

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerSet> = StreamCodec.composite(
            ByteBufCodecs.INT, WarTimerSet::time, ::WarTimerSet
        )

        fun handleClientboundPacket(data: WarTimerSet, context: IPayloadContext) {
            context.enqueueWork {
                WarOverlay.setTimer = 50
                WarOverlay.timeLeft = data.time

                val player = rgMinecraft.player ?: return@enqueueWork
                player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 0.8f)
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}
