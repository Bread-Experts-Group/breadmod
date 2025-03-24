package org.bread_experts_group.breadmod.network.clientbound

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode

class ToolGunModeSyncPacket(private val mode: IToolGunMode, private val tag: CompoundTag) : CustomPacketPayload {
	constructor(mode: IToolGunMode) : this(mode, mode.getUpdateTag())

	companion object {
		val TYPE: CustomPacketPayload.Type<ToolGunModeSyncPacket> =
			CustomPacketPayload.Type(modLocation("tool_gun_sync_packet"))
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ToolGunModeSyncPacket> = StreamCodec.composite(
			IToolGunMode.STREAM_CODEC, ToolGunModeSyncPacket::mode,
			ByteBufCodecs.TRUSTED_COMPOUND_TAG, ToolGunModeSyncPacket::tag,
			::ToolGunModeSyncPacket
		)

		fun handleClientboundPacket(data: ToolGunModeSyncPacket, context: IPayloadContext) {
			LogManager.getLogger("ToolGunModeSyncPacket/handleClientboundPacket")
				.info("receiving packet from server: ${data.tag}")
			data.mode.loadAdditional(data.tag)
		}
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}