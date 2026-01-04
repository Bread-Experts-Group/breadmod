package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.client.ClientMicroLevel

data class EncapsulateBlockUpdatePhysicsGridPacket(
	val id: Long,
	val encapsulate: ClientboundBlockUpdatePacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateBlockUpdatePhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("enc_blk_upd_phys_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateBlockUpdatePhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateBlockUpdatePhysicsGridPacket::id,
				ClientboundBlockUpdatePacket.STREAM_CODEC, EncapsulateBlockUpdatePhysicsGridPacket::encapsulate,
				::EncapsulateBlockUpdatePhysicsGridPacket
			)

		fun handleClientboundPacket(data: EncapsulateBlockUpdatePhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.clientGrids[data.id] ?: return@enqueueWork
				(grid.microLevel as ClientMicroLevel).setServerVerifiedBlockState(
					data.encapsulate.pos,
					data.encapsulate.blockState,
					19
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