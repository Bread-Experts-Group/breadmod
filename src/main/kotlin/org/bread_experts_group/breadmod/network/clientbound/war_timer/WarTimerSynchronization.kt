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

internal data class WarTimerSynchronization(val time: Int) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<WarTimerSynchronization> =
            CustomPacketPayload.Type(modLocation("war_timer_sync"))

        val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerSynchronization> = StreamCodec.composite(
            ByteBufCodecs.VAR_INT, WarTimerSynchronization::time, ::WarTimerSynchronization
        )

        fun handleClientboundPacket(data: WarTimerSynchronization, context: IPayloadContext) {
            context.enqueueWork {
                WarOverlay.timeLeft = data.time

                val player = rgMinecraft.player ?: return@enqueueWork
                player.playSound(ModSounds.WAR_TIMER.get(), 0.8f, 1f)
            }
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}