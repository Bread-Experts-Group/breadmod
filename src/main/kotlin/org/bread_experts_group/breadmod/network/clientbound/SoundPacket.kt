package org.bread_experts_group.breadmod.network.clientbound

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.sounds.SoundEvent
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance

class SoundPacket(
	private val sound: Holder<SoundEvent>,
	private val pos: BlockPos,
	private val falloffDistance: Double
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<SoundPacket> =
			CustomPacketPayload.Type(modLocation("sound_packet"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SoundPacket> = StreamCodec.composite(
			SoundEvent.STREAM_CODEC, SoundPacket::sound,
			BlockPos.STREAM_CODEC, SoundPacket::pos,
			ByteBufCodecs.DOUBLE, SoundPacket::falloffDistance,
			::SoundPacket
		)

		fun handleClientboundPacket(data: SoundPacket, context: IPayloadContext) {
			localClient.soundManager.play(StereoSoundInstance(data.sound.value(), data.pos, data.falloffDistance))
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}