package org.bread_experts_group.breadmod.network.clientbound.screen_bleed

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay

class ScreenBleedSet(private val seconds: Int) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ScreenBleedSet> =
			CustomPacketPayload.Type(modLocation("screen_bleed_set"))
		val STREAM_CODEC: StreamCodec<ByteBuf, ScreenBleedSet> = StreamCodec.composite(
			ByteBufCodecs.INT, ScreenBleedSet::seconds, ::ScreenBleedSet
		)

		fun handleClientboundPacket(data: ScreenBleedSet, context: IPayloadContext) {
			context.enqueueWork {
				ScreenBleedOverlay.maxProgress = data.seconds
			}
		}
	}
	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}