package org.bread_experts_group.breadmod.network.clientbound

import com.mojang.authlib.GameProfile
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.buffer.render.MachTrailBufferTask.machTrailMap
import org.bread_experts_group.breadmod.data_holders.client.MachTrailData

data class MachTrailPacket(private val playerProfile: GameProfile) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<MachTrailPacket> =
			CustomPacketPayload.Type(modLocation("mach_trail_packet"))
		val STREAM_CODEC: StreamCodec<ByteBuf, MachTrailPacket> = StreamCodec.composite(
			ByteBufCodecs.GAME_PROFILE, MachTrailPacket::playerProfile, ::MachTrailPacket
		)

		fun handleClientboundPacket(data: MachTrailPacket, context: IPayloadContext) {
			val targetPlayer = context.player().level().getPlayerByUUID(data.playerProfile.id) ?: return
			machTrailMap[targetPlayer] = MachTrailData(targetPlayer)
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}