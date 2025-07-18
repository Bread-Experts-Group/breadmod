package org.bread_experts_group.breadmod.network.clientbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntityNew

class DoubleOrNothingPacketNew(
	private val pos: BlockPos,
	private val nothing: Boolean = false
) : CustomPacketPayload {
	companion object {
		private val TYPE: CustomPacketPayload.Type<DoubleOrNothingPacketNew> =
			CustomPacketPayload.Type(modLocation("double_or_nothing_packet"))
		private val STREAM_CODEC: StreamCodec<ByteBuf, DoubleOrNothingPacketNew> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, DoubleOrNothingPacketNew::pos,
			ByteBufCodecs.BOOL, DoubleOrNothingPacketNew::nothing,
			::DoubleOrNothingPacketNew
		)

		private fun handleClientboundPacket(data: DoubleOrNothingPacketNew, context: IPayloadContext) {
			context.enqueueWork {
				val level = context.player().level() ?: return@enqueueWork
				val entity = level.getBlockEntity(data.pos) as DoubleOrNothingBlockEntityNew
				val playerData = entity.data ?: return@enqueueWork
				entity.handleDouble(playerData, level, context.player(), data.nothing)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}