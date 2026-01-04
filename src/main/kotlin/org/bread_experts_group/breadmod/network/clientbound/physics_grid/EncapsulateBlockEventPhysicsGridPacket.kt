package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundBlockEventPacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

data class EncapsulateBlockEventPhysicsGridPacket(
	val id: Long,
	val encapsulate: ClientboundBlockEventPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateBlockEventPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("enc_blk_evt_phys_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateBlockEventPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateBlockEventPhysicsGridPacket::id,
				ClientboundBlockEventPacket.STREAM_CODEC, EncapsulateBlockEventPhysicsGridPacket::encapsulate,
				::EncapsulateBlockEventPhysicsGridPacket
			)

		fun handleClientboundPacket(data: EncapsulateBlockEventPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.clientGrids[data.id] ?: return@enqueueWork
				grid.microLevel.blockEvent(
					data.encapsulate.pos,
					data.encapsulate.block,
					data.encapsulate.b0,
					data.encapsulate.b1
				)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToClient(
			this.TYPE, this.STREAM_CODEC,
			this::handleClientboundPacket
		)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}