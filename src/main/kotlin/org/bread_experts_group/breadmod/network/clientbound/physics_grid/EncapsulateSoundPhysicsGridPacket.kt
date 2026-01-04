package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundSoundPacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

data class EncapsulateSoundPhysicsGridPacket(
	val id: Long,
	val encapsulate: ClientboundSoundPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateSoundPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("new_phys_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateSoundPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateSoundPhysicsGridPacket::id,
				ClientboundSoundPacket.STREAM_CODEC, EncapsulateSoundPhysicsGridPacket::encapsulate,
				::EncapsulateSoundPhysicsGridPacket
			)

		fun handleClientboundPacket(data: EncapsulateSoundPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				TODO("Encapsulate $data")
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToClient(
			this.TYPE, this.STREAM_CODEC,
			this::handleClientboundPacket
		)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}