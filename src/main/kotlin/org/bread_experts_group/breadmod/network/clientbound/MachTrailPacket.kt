package org.bread_experts_group.breadmod.network.clientbound

import com.mojang.authlib.GameProfile
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.MachTrailData
import org.bread_experts_group.breadmod.util.render.machTrailMap

data class MachTrailPacket(
	private val playerProfile : GameProfile
) : CustomPacketPayload {
	companion object {
		val TYPE : CustomPacketPayload.Type<MachTrailPacket> =
			CustomPacketPayload.Type(modLocation("mach_trail_packet"))
		val STREAM_CODEC : StreamCodec<ByteBuf, MachTrailPacket> = StreamCodec.composite(
			ByteBufCodecs.GAME_PROFILE, MachTrailPacket::playerProfile, ::MachTrailPacket
		)

		fun handleClientboundPacket(data : MachTrailPacket, context : IPayloadContext) {
			machTrailMap[data.playerProfile] = MachTrailData(data.playerProfile)
		}
	}

	override fun type() : CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}