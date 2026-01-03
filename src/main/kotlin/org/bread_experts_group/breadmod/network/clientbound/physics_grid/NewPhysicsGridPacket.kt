package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevel

data class NewPhysicsGridPacket(val position: Vec3, val bounding: AABB) : CustomPacketPayload {
	constructor(
		position: Vec3,
		center: Vec3,
		size: Vec3
	) : this(position, AABB.ofSize(center, size.x, size.y, size.z))

	companion object {
		val TYPE: CustomPacketPayload.Type<NewPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("new_phys_grid"))
		val STREAM_CODEC: StreamCodec<ByteBuf, NewPhysicsGridPacket> = StreamCodec.composite(
			ByteBufCodecs.fromCodec(Vec3.CODEC), NewPhysicsGridPacket::position,
			ByteBufCodecs.fromCodec(Vec3.CODEC), { it.bounding.center },
			ByteBufCodecs.fromCodec(Vec3.CODEC), {
				Vec3(it.bounding.xsize, it.bounding.ysize, it.bounding.zsize)
			}, ::NewPhysicsGridPacket
		)

		fun handleClientboundPacket(data: NewPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val newGrid = PhysicsGrid(data.position, data.bounding)
				newGrid.microLevel = ClientMicroLevel(newGrid)
				PhysicsGrid.localGrids.add(newGrid)
				newGrid.attachRenderer()
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}