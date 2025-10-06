package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.BreadModBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class BreadModBEUpdateRequestPacket(
	private val pos: BlockPos
) : CustomPacketPayload {
	companion object {
		val TYPE: Type<BreadModBEUpdateRequestPacket> =
			Type(modLocation("block_entity_update_request"))
		val STREAM_CODEC: StreamCodec<ByteBuf, BreadModBEUpdateRequestPacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, BreadModBEUpdateRequestPacket::pos,
			::BreadModBEUpdateRequestPacket
		)

		fun handleServerboundPacket(data: BreadModBEUpdateRequestPacket, context: IPayloadContext) {
			val level = context.player().level() as ServerLevel
			if (
				level.chunkSource.chunkMap
					.getPlayers(ChunkPos(data.pos), false)
					.contains(context.player())
			) {
				val entity = level.getBlockEntity(data.pos) as? BreadModBlockEntity ?: return
				(entity.blockState.block as BreadModBlock).synchronizeEntity(entity)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToServer(
			this.TYPE, this.STREAM_CODEC,
			this::handleServerboundPacket
		)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}