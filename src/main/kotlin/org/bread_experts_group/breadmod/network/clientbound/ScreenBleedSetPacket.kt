package org.bread_experts_group.breadmod.network.clientbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.ScreenBleedOverlay
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.ACTIVE
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.BLUE_SCREEN
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.MAX_PROGRESS
import org.bread_experts_group.breadmod.network.clientbound.ScreenBleedSetPacket.ScreenBleedSetType.PROGRESS

class ScreenBleedSetPacket(
	private val enumType: ScreenBleedSetType,
	private val number: Float = 0f,
	private val bool: Boolean = false
) : CustomPacketPayload {
	enum class ScreenBleedSetType { PROGRESS, MAX_PROGRESS, ACTIVE, BLUE_SCREEN }
	companion object {
		val TYPE: CustomPacketPayload.Type<ScreenBleedSetPacket> =
			CustomPacketPayload.Type(modLocation("screen_bleed_set_packet"))
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ScreenBleedSetPacket> = StreamCodec.composite(
			NeoForgeStreamCodecs.enumCodec(ScreenBleedSetType::class.java), ScreenBleedSetPacket::enumType,
			ByteBufCodecs.FLOAT, ScreenBleedSetPacket::number,
			ByteBufCodecs.BOOL, ScreenBleedSetPacket::bool,
			::ScreenBleedSetPacket
		)

		fun handleClientboundPacket(data: ScreenBleedSetPacket, context: IPayloadContext) {
			context.enqueueWork {
				when (data.enumType) {
					PROGRESS     -> ScreenBleedOverlay.progress = data.number
					MAX_PROGRESS -> ScreenBleedOverlay.maxProgress = data.number
					ACTIVE       -> ScreenBleedOverlay.active = data.bool
					BLUE_SCREEN  -> ScreenBleedOverlay.overrideDeathScreen = data.bool
				}
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}