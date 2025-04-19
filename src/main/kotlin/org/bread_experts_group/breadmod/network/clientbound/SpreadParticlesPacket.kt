package org.bread_experts_group.breadmod.network.clientbound

import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.level.Level
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.joml.Vector3f

class SpreadParticlesPacket private constructor(
	val level: String,
	val particleType: ParticleOptions,
	val position: Vector3f,
	val radius: Float
) : CustomPacketPayload {
	constructor(level: Level, particleType: ParticleOptions, position: Vector3f, radius: Float) : this(
		level.dimension().location().toString(),
		particleType, position, radius
	)

	companion object {
		val TYPE: CustomPacketPayload.Type<SpreadParticlesPacket> =
			CustomPacketPayload.Type(modLocation("spread_particles"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, SpreadParticlesPacket> = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, SpreadParticlesPacket::level,
			ParticleTypes.STREAM_CODEC, SpreadParticlesPacket::particleType,
			ByteBufCodecs.VECTOR3F, SpreadParticlesPacket::position,
			ByteBufCodecs.FLOAT, SpreadParticlesPacket::radius,
			::SpreadParticlesPacket
		)

		fun handleClientboundPacket(data: SpreadParticlesPacket, context: IPayloadContext) {
			context.enqueueWork {
				context.player().level().let {
					if (data.level != it.dimension().location().toString()) return@let
					val random = it.random
					repeat((data.radius * 2).toInt()) { _ ->
						it.addParticle(
							data.particleType,
							data.position.x + ((random.nextFloat() - 0.5) * data.radius * 2),
							data.position.y + ((random.nextFloat() - 0.5) * data.radius * 2),
							data.position.z + ((random.nextFloat() - 0.5) * data.radius * 2),
							0.0, 0.0, 0.0
						)
					}
				}
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}