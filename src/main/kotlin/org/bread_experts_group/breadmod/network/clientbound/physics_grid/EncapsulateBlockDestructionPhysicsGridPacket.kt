package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

data class EncapsulateBlockDestructionPhysicsGridPacket(
	val id: Long,
	val encapsulate: ClientboundBlockDestructionPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateBlockDestructionPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("enc_blk_dst_phys_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateBlockDestructionPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateBlockDestructionPhysicsGridPacket::id,
				ClientboundBlockDestructionPacket.STREAM_CODEC,
				EncapsulateBlockDestructionPhysicsGridPacket::encapsulate,
				::EncapsulateBlockDestructionPhysicsGridPacket
			)

		fun handleClientboundPacket(data: EncapsulateBlockDestructionPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.clientGrids[data.id] ?: return@enqueueWork
				grid.microLevel.destroyBlockProgress(
					data.encapsulate.id,
					data.encapsulate.pos,
					data.encapsulate.progress
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