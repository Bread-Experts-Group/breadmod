package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevel
import org.bread_experts_group.breadmod.experimental.physics_grid.render.GridMesh
import org.bread_experts_group.breadmod.network.BreadModCodecs

data class NewPhysicsGridPacket(
	val id: Long,
	val position: Vec3,
	val bounding: AABB,
	val posA: BlockPos,
	val posB: BlockPos
) : CustomPacketPayload {
	constructor(
		id: Long,
		position: Vec3,
		center: Vec3,
		size: Vec3,
		posA: BlockPos,
		posB: BlockPos
	) : this(id, position, AABB.ofSize(center, size.x, size.y, size.z), posA, posB)

	companion object {
		val TYPE: CustomPacketPayload.Type<NewPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("new_phys_grid"))
		val STREAM_CODEC: StreamCodec<ByteBuf, NewPhysicsGridPacket> = StreamCodec.composite(
			ByteBufCodecs.VAR_LONG, NewPhysicsGridPacket::id,
			BreadModCodecs.VEC3_STREAM_CODEC, NewPhysicsGridPacket::position,
			BreadModCodecs.VEC3_STREAM_CODEC, { (_, _, bounding, _, _) -> bounding.center },
			BreadModCodecs.VEC3_STREAM_CODEC, { (_, _, bounding, _, _) ->
				Vec3(bounding.xsize, bounding.ysize, bounding.zsize)
			},
			BlockPos.STREAM_CODEC, NewPhysicsGridPacket::posA,
			BlockPos.STREAM_CODEC, NewPhysicsGridPacket::posB,
			::NewPhysicsGridPacket
		)

		fun handleClientboundPacket(data: NewPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val level = localClient.level ?: return@enqueueWork
				val newGrid = PhysicsGrid(data.id, data.position, data.bounding, level)
				val (blocks, blockEntities) = PhysicsGrid.collectBlocksAndEntities(data.posA, data.posB, level)
				newGrid.microLevel = ClientMicroLevel(level, newGrid)
				blocks.forEach { (pos, state) -> newGrid.microLevel.setBlock(pos, state, 0) }
				blockEntities.forEach { (_, blockEntity) -> newGrid.microLevel.setBlockEntity(blockEntity) }
				PhysicsGrid.clientGrids[data.id] = newGrid
				GridMesh.create(newGrid)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}