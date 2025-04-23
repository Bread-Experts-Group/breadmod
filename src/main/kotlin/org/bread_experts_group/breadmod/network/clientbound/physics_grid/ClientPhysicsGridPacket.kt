package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import io.netty.buffer.ByteBuf
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.physics_grid.ClientPhysicsGrid

class ClientPhysicsGridPacket(
	private val posA: BlockPos,
	private val posB: BlockPos
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ClientPhysicsGridPacket> = CustomPacketPayload.Type(
			modLocation("client_physics_packet")
		)
		val STREAM_CODEC: StreamCodec<ByteBuf, ClientPhysicsGridPacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ClientPhysicsGridPacket::posA,
			BlockPos.STREAM_CODEC, ClientPhysicsGridPacket::posB,
			::ClientPhysicsGridPacket
		)

		fun handleClientboundPacket(data: ClientPhysicsGridPacket, context: IPayloadContext) {
			val level = context.player().level() as ClientLevel
			ClientPhysicsGrid(level)
				.setGridData(data.posA, data.posB)
				.setBlockData(data.posA, data.posB)
				.setPos(context.player().position())
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}