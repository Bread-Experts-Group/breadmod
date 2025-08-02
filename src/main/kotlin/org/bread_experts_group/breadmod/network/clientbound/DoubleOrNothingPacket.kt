package org.bread_experts_group.breadmod.network.clientbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.actual.DoubleOrNothingBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.state.DoubleOrNothingStateHandler

class DoubleOrNothingPacket(
	private val pos: BlockPos,
	private val nothing: Boolean = false
) : CustomPacketPayload {
	companion object {
		private val TYPE: CustomPacketPayload.Type<DoubleOrNothingPacket> =
			CustomPacketPayload.Type(modLocation("double_or_nothing_packet"))
		private val STREAM_CODEC: StreamCodec<ByteBuf, DoubleOrNothingPacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, DoubleOrNothingPacket::pos,
			ByteBufCodecs.BOOL, DoubleOrNothingPacket::nothing,
			::DoubleOrNothingPacket
		)

		private fun handleClientboundPacket(data: DoubleOrNothingPacket, context: IPayloadContext) {
			context.enqueueWork {
				val level = context.player().level() ?: return@enqueueWork
				val entity = level.getBlockEntity(data.pos) as BreadModBlockEntity
				val state = entity.getCapability(DoubleOrNothingStateHandler.BLOCK_VOID)
				(ModBlocks.DOUBLE_OR_NOTHING.get().block as DoubleOrNothingBlock).handleDouble(
					entity, state, data.nothing
				)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}