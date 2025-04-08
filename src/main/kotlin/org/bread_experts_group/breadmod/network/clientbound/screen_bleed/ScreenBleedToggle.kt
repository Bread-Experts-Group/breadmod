package org.bread_experts_group.breadmod.network.clientbound.screen_bleed

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay

class ScreenBleedToggle(private val active: Boolean, private val reset: Boolean) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ScreenBleedToggle> =
			CustomPacketPayload.Type(modLocation("screen_bleed_toggle"))
		val STREAM_CODEC: StreamCodec<ByteBuf, ScreenBleedToggle> = StreamCodec.composite(
			ByteBufCodecs.BOOL, ScreenBleedToggle::active,
			ByteBufCodecs.BOOL, ScreenBleedToggle::reset,
			::ScreenBleedToggle
		)

		fun handleClientboundPacket(data: ScreenBleedToggle, context: IPayloadContext) {
			context.enqueueWork {
				if (data.reset) {
					ScreenBleedOverlay.active = false
					ScreenBleedOverlay.progress = 0
					ScreenBleedOverlay.maxProgress = 0
				} else ScreenBleedOverlay.active = data.active
			}
		}
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}