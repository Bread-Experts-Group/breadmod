package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevel
import org.bread_experts_group.breadmod.network.BreadModCodecs

data class NewPhysicsGridPacket(
	val id: Long,
	val position: Vec3,
	val bounding: AABB,
	val immediateBlocks: Map<BlockPos, BlockState>
) : CustomPacketPayload {
	constructor(
		id: Long,
		position: Vec3,
		center: Vec3,
		size: Vec3,
		immediateBlocks: Map<BlockPos, BlockState>
	) : this(id, position, AABB.ofSize(center, size.x, size.y, size.z), immediateBlocks)

	companion object {
		val TYPE: CustomPacketPayload.Type<NewPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("new_phys_grid"))
		val STREAM_CODEC: StreamCodec<ByteBuf, NewPhysicsGridPacket> = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, NewPhysicsGridPacket::id,
			ByteBufCodecs.fromCodec(Vec3.CODEC), NewPhysicsGridPacket::position,
			ByteBufCodecs.fromCodec(Vec3.CODEC), { (_, _, bounding, _) -> bounding.center },
			ByteBufCodecs.fromCodec(Vec3.CODEC), { (_, _, bounding, _) ->
				Vec3(bounding.xsize, bounding.ysize, bounding.zsize)
			},
			ByteBufCodecs.map(
				::HashMap,
				BlockPos.STREAM_CODEC,
				BreadModCodecs.BLOCKSTATE_STREAM_CODEC
			), NewPhysicsGridPacket::immediateBlocks,
			::NewPhysicsGridPacket
		)

		fun handleClientboundPacket(data: NewPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val newGrid = PhysicsGrid(data.position, data.bounding)
				newGrid.microLevel = ClientMicroLevel(localClient.level ?: return@enqueueWork, newGrid)
				data.immediateBlocks.forEach { (pos, state) -> newGrid.microLevel.setBlock(pos, state, 0) }
				PhysicsGrid.localGrids[data.id] = newGrid
				newGrid.attachRenderer()
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}