package org.bread_experts_group.breadmod.network.clientbound.screen_bleed

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay

class ScreenBleedSynchronization(private val time: Int) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ScreenBleedSynchronization> =
			CustomPacketPayload.Type(modLocation("screen_bleed_sync"))
		val STREAM_CODEC: StreamCodec<ByteBuf, ScreenBleedSynchronization> = StreamCodec.composite(
			ByteBufCodecs.VAR_INT, ScreenBleedSynchronization::time, ::ScreenBleedSynchronization
		)

		fun handleClientboundPacket(data: ScreenBleedSynchronization, context: IPayloadContext) {
			context.enqueueWork {
				ScreenBleedOverlay.progress = data.time
			}
		}
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}