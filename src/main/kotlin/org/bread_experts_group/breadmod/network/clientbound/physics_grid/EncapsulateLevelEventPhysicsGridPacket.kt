package org.bread_experts_group.breadmod.network.clientbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ClientboundLevelEventPacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

data class EncapsulateLevelEventPhysicsGridPacket(
	val id: Long,
	val encapsulate: ClientboundLevelEventPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulateLevelEventPhysicsGridPacket> =
			CustomPacketPayload.Type(modLocation("enc_lvl_evt_phys_grid"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulateLevelEventPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulateLevelEventPhysicsGridPacket::id,
				ClientboundLevelEventPacket.STREAM_CODEC, EncapsulateLevelEventPhysicsGridPacket::encapsulate,
				::EncapsulateLevelEventPhysicsGridPacket
			)

		fun handleClientboundPacket(data: EncapsulateLevelEventPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.clientGrids[data.id] ?: return@enqueueWork
				if (data.encapsulate.isGlobalEvent) grid.microLevel.globalLevelEvent(
					data.encapsulate.type,
					data.encapsulate.pos,
					data.encapsulate.data
				) else grid.microLevel.levelEvent(
					data.encapsulate.type,
					data.encapsulate.pos,
					data.encapsulate.data
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