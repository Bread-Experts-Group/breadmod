package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.network.clientbound.PhysicsGridPacket
import org.joml.Vector3f
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.toVec3
import thedarkcolour.kotlinforforge.neoforge.forge.vectorutil.v3d.unaryMinus

class PhysicsGridRequestPacket(
	val position: Vector3f,
	val from: BlockPos,
	val to: BlockPos
) : CustomPacketPayload {
	companion object {
		val TYPE: Type<PhysicsGridRequestPacket> = Type(modLocation("physics_grid_request"))
		val STREAM_CODEC: StreamCodec<ByteBuf, PhysicsGridRequestPacket> = StreamCodec.composite(
			ByteBufCodecs.VECTOR3F, PhysicsGridRequestPacket::position,
			BlockPos.STREAM_CODEC, PhysicsGridRequestPacket::from,
			BlockPos.STREAM_CODEC, PhysicsGridRequestPacket::to,
			::PhysicsGridRequestPacket
		)
		val gridShapes: MutableList<List<VoxelShape>> = mutableListOf()

		fun handleServerboundPacket(data: PhysicsGridRequestPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = buildMap<BlockPos, BlockState> {
					BlockPos.betweenClosedStream(
						AABB.encapsulatingFullBlocks(data.from, data.to)
					).forEach {
						val state = context.player().level().getBlockState(it)
						if (state.renderShape == RenderShape.INVISIBLE) return@forEach
						// Reconstruction of the BlockPos must happen, otherwise hash codes will get scrambled.
						this[BlockPos(it)] = state
					}
				}
				this.gridShapes.add(
					buildList {
						grid.forEach { (pos, state) ->
							val offset = pos.offset(-data.from).toVec3()
							this.add(
								state.getCollisionShape(
									context.player().level(),
									BlockPos.containing(pos.offset(-data.from).toVec3()),
									CollisionContext.of(context.player())
								).move(
									data.position.x.toDouble() + offset.x,
									data.position.y.toDouble() + offset.y,
									data.position.z.toDouble() + offset.z
								)
							)
						}
					}
				)
				PacketDistributor.sendToAllPlayers(
					PhysicsGridPacket(
						context.player().level(),
						grid,
						data.position,
						data.from
					)
				)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToServer(
			this.TYPE, this.STREAM_CODEC,
			this::handleServerboundPacket
		)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}