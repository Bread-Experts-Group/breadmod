package org.bread_experts_group.breadmod.network.clientbound

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance
import net.minecraft.client.resources.sounds.SoundInstance
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.levelgen.SingleThreadedRandomSource
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.network.serverbound.GasGasGasNukePacket
import org.bread_experts_group.breadmod.registry.sound.ModSounds

class GasGasGasSoundPacket(
	val forEntityID: Int
) : CustomPacketPayload {
	companion object {
		class GasGasGasSoundInstance(val entity: Entity) : AbstractTickableSoundInstance(
			ModSounds.GAS_GAS_GAS.get(), SoundSource.PLAYERS,
			SingleThreadedRandomSource(-23492351)
		) {
			init {
				this.looping = false
				this.relative = true
				this.attenuation = SoundInstance.Attenuation.LINEAR
			}

			private var safePeriod: Int = 80
			override fun tick() {
				this.x = this.entity.x
				this.y = this.entity.y
				this.z = this.entity.z
				if (this.entity.deltaMovement.length() < 0.1) this.safePeriod--
				else this.safePeriod = 20
				if (this.safePeriod <= 0) {
					this.stop()
					PacketDistributor.sendToServer(GasGasGasNukePacket())
				}
			}
		}

		val TYPE: CustomPacketPayload.Type<GasGasGasSoundPacket> =
			CustomPacketPayload.Type(modLocation("gas_gas_gas"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, GasGasGasSoundPacket> = StreamCodec.composite(
			ByteBufCodecs.INT, GasGasGasSoundPacket::forEntityID,
			::GasGasGasSoundPacket
		)

		fun handleClientboundPacket(data: GasGasGasSoundPacket, context: IPayloadContext) {
			context.enqueueWork {
				val level = context.player().level() ?: return@enqueueWork
				val entity = level.getEntity(data.forEntityID) ?: return@enqueueWork
				localClient.soundManager.play(GasGasGasSoundInstance(entity))
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}