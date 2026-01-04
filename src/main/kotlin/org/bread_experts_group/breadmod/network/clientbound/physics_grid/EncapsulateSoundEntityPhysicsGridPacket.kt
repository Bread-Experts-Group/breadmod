package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation

data class EncapsulateSoundEntityPhysicsGridPacket(
	val id: Long,
	val encapsulate: ClientboundSoundEntityPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateSoundEntityPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("enc_snd_ent_phys_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateSoundEntityPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateSoundEntityPhysicsGridPacket::id,
				ClientboundSoundEntityPacket.STREAM_CODEC, EncapsulateSoundEntityPhysicsGridPacket::encapsulate,
				::EncapsulateSoundEntityPhysicsGridPacket
			)

		fun handleClientboundPacket(data: EncapsulateSoundEntityPhysicsGridPacket, context: IPayloadContext) {
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