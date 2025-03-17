package org.bread_experts_group.breadmod.network.clientbound.war_timer

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay

internal data class WarTimerToggle(private val active: Boolean) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<WarTimerToggle> =
			CustomPacketPayload.Type(modLocation("war_timer_toggle"))
		val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerToggle> = StreamCodec.composite(
			ByteBufCodecs.BOOL, WarTimerToggle::active, ::WarTimerToggle
		)

		fun handleClientboundPacket(data: WarTimerToggle, context: IPayloadContext) {
			context.enqueueWork {
				WarOverlay.timerActive = data.active
			}
		}
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}