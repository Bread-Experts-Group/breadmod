package org.bread_experts_group.breadmod.network.clientbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class BreadModBlockEntityUpdatePacket(
	private val pos: BlockPos,
	private val tag: CompoundTag
) : CustomPacketPayload {
	companion object {
		private val TYPE: CustomPacketPayload.Type<BreadModBlockEntityUpdatePacket> = CustomPacketPayload.Type(
			modLocation("block_entity_update")
		)
		private val STREAM_CODEC: StreamCodec<ByteBuf, BreadModBlockEntityUpdatePacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, BreadModBlockEntityUpdatePacket::pos,
			ByteBufCodecs.TRUSTED_COMPOUND_TAG, BreadModBlockEntityUpdatePacket::tag,
			::BreadModBlockEntityUpdatePacket
		)

		private fun handleClientboundPacket(data: BreadModBlockEntityUpdatePacket, context: IPayloadContext) {
			context.enqueueWork {
				val level = context.player().level() ?: return@enqueueWork
				val entity = level.getBlockEntity(data.pos) as? BreadModBlockEntity ?: return@enqueueWork
				entity.loadCustomOnly(data.tag, level.registryAccess())
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToClient(
			this.TYPE, this.STREAM_CODEC,
			this::handleClientboundPacket
		)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}