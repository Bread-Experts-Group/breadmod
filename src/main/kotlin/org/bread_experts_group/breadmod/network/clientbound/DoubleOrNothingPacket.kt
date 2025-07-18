package org.bread_experts_group.breadmod.network.clientbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.DoubleOrNothingBlockEntity
import org.bread_experts_group.breadmod.util.toList

class DoubleOrNothingPacket(
	private val zoomAndTilt: List<Float>,
	private val nothingJackpotCashout: List<Boolean>,
	private val counter: Int,
	private val pos: BlockPos
) : CustomPacketPayload {
	companion object {
		private val TYPE: CustomPacketPayload.Type<DoubleOrNothingPacket> =
			CustomPacketPayload.Type(modLocation("double_or_nothing_packet"))
		private val STREAM_CODEC: StreamCodec<ByteBuf, DoubleOrNothingPacket> = StreamCodec.composite(
			ByteBufCodecs.FLOAT.toList(), DoubleOrNothingPacket::zoomAndTilt,
			ByteBufCodecs.BOOL.toList(), DoubleOrNothingPacket::nothingJackpotCashout,
			ByteBufCodecs.INT, DoubleOrNothingPacket::counter,
			BlockPos.STREAM_CODEC, DoubleOrNothingPacket::pos,
			::DoubleOrNothingPacket
		)

		private fun handleClientboundPacket(data: DoubleOrNothingPacket, context: IPayloadContext) {
			val level = context.player().level() ?: return
			val entity = level.getBlockEntity(data.pos) as DoubleOrNothingBlockEntity
			entity.zoom = data.zoomAndTilt[0]
			entity.tilt = data.zoomAndTilt[1]
			entity.counter = data.counter
			entity.nothing = data.nothingJackpotCashout[0]
			entity.hasJackpot = data.nothingJackpotCashout[1]
			entity.cashout = data.nothingJackpotCashout[2]
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}