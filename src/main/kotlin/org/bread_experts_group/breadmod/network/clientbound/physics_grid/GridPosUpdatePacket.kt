package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.network.BreadModCodecs

class GridPosUpdatePacket(
	private val newPos: Vec3,
	private val id: Int
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<GridPosUpdatePacket> =
			CustomPacketPayload.Type(modLocation("grid_pos_update"))
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, GridPosUpdatePacket> = StreamCodec.composite(
			BreadModCodecs.VEC3, GridPosUpdatePacket::newPos,
			ByteBufCodecs.INT, GridPosUpdatePacket::id,
			::GridPosUpdatePacket
		)

		fun handleClientboundPacket(data: GridPosUpdatePacket, context: IPayloadContext) {
//			PhysicsGridGlobals.grids[data.id]?.setPos(data.newPos)
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}