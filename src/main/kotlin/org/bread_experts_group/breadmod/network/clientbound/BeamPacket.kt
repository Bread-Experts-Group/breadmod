package org.bread_experts_group.breadmod.network.clientbound

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.buffer.render.BeamBufferTask
import org.joml.Vector3f

class BeamPacket(
	private val start: Vector3f,
	private val end: Vector3f,
	private val thickness: Float?
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<BeamPacket> =
			CustomPacketPayload.Type(modLocation("beam_packet"))
		val STREAM_CODEC: StreamCodec<ByteBuf, BeamPacket> = StreamCodec.composite(
			ByteBufCodecs.VECTOR3F, BeamPacket::start,
			ByteBufCodecs.VECTOR3F, BeamPacket::end,
			ByteBufCodecs.FLOAT, BeamPacket::thickness,
			::BeamPacket
		)

		fun handleClientboundPacket(data: BeamPacket, context: IPayloadContext) {
			context.enqueueWork {
				BeamBufferTask.create(data.start, data.end, data.thickness)
			}
		}
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}