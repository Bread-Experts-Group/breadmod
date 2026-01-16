package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.experimental.physics_grid.render.GridMesh
import org.bread_experts_group.breadmod.network.payloadType
import org.bread_experts_group.breadmod.util.getVec3
import org.bread_experts_group.breadmod.util.plus
import org.bread_experts_group.breadmod.util.putVec3
import java.util.function.BiFunction

sealed class GridPacket(
	private val type: GridPacketType,
	private val data: CompoundTag
) : CustomPacketPayload {
	companion object {
		private val TYPE_CLIENT: CustomPacketPayload.Type<GridPacket.Client> = payloadType("grid_packet_client")
		private val TYPE_SERVER: CustomPacketPayload.Type<GridPacket.Server> = payloadType("grid_packet_server")
		private val STREAM_CODEC_CLIENT: StreamCodec<FriendlyByteBuf, Client> = this.streamCodec(::Client)
		private val STREAM_CODEC_SERVER: StreamCodec<FriendlyByteBuf, Server> = this.streamCodec(::Server)

		private fun <T : GridPacket> streamCodec(function: BiFunction<GridPacketType, CompoundTag, T>): StreamCodec<FriendlyByteBuf, T> =
			StreamCodec.composite(
				NeoForgeStreamCodecs.enumCodec(GridPacketType::class.java), GridPacket::type,
				ByteBufCodecs.TRUSTED_COMPOUND_TAG, GridPacket::data,
				function
			)

		private fun handleClientbound(packet: GridPacket, context: IPayloadContext) {
			when (packet.type) {
				GridPacketType.MOVEMENT -> {
					val grid = packet.getGrid(context) ?: return
					val delta = packet.data.getVec3("delta")
					grid.delta += delta
				}
				GridPacketType.CLEAR -> {
					val id = packet.data.getLong("grid_id")
					GridMesh.meshes.remove(PhysicsGrid.clientGrids.remove(id))?.close()
				}
				GridPacketType.UPDATE_RENDER -> {
					GridMesh.meshes[packet.getGrid(context)]?.recompile()
				}
			}
		}

		private fun handleServerbound(packet: GridPacket, context: IPayloadContext) {
			when (packet.type) {
				GridPacketType.MOVEMENT -> {
					val grid = packet.getGrid(context) ?: return
					val delta = packet.data.getVec3("delta")
					grid.delta += delta
					PacketDistributor.sendToAllPlayers(Client(packet.type, packet.data))
				}
				GridPacketType.CLEAR -> {
					PhysicsGrid.serverGrids.remove(packet.data.getLong("grid_id"))
					PacketDistributor.sendToAllPlayers(Client(packet.type, packet.data))
				}
				GridPacketType.UPDATE_RENDER -> {}
			}
		}

		fun relayGridMovement(delta: Vec3, gridID: Long) {
			val tag = CompoundTag()
			tag.putVec3("delta", delta)
			tag.putLong("grid_id", gridID)
			PacketDistributor.sendToServer(Server(GridPacketType.MOVEMENT, tag))
		}

		fun clearGrid(gridID: Long) {
			val tag = CompoundTag()
			tag.putLong("grid_id", gridID)
			PacketDistributor.sendToServer(Server(GridPacketType.CLEAR, tag))
		}

		fun recompileGridMeshOnClient(gridID: Long) {
			val tag = CompoundTag()
			tag.putLong("grid_id", gridID)
			PacketDistributor.sendToAllPlayers(Client(GridPacketType.UPDATE_RENDER, tag))
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar
				.playToClient(this.TYPE_CLIENT, this.STREAM_CODEC_CLIENT, this::handleClientbound)
				.playToServer(this.TYPE_SERVER, this.STREAM_CODEC_SERVER, this::handleServerbound)
	}

	enum class GridPacketType { MOVEMENT, CLEAR, UPDATE_RENDER }

	private class Client(type: GridPacketType, data: CompoundTag) : GridPacket(type, data) {
		override fun type(): CustomPacketPayload.Type<Client> = Companion.TYPE_CLIENT
	}

	private class Server(type: GridPacketType, data: CompoundTag) : GridPacket(type, data) {
		override fun type(): CustomPacketPayload.Type<Server> = Companion.TYPE_SERVER
	}

	private fun getGrid(context: IPayloadContext): PhysicsGrid? {
		val level = context.player().level()
		if (!this.data.contains("grid_id")) throw NullPointerException("Grid ID is somehow not in packet data?")
		val id = this.data.getLong("grid_id")
		val grid = if (level.isClientSide) PhysicsGrid.clientGrids[id] else PhysicsGrid.serverGrids[id]
		if (grid == null) LogManager.getLogger("GridPacket").warn("Grid is null from provided id...")
		return grid
	}
}