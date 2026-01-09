package org.bread_experts_group.breadmod.network.serverbound.physics_grid

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid
import org.bread_experts_group.breadmod.network.payloadType

data class EncapsulatePlayerActionPhysicsGridPacket(
	val id: Long,
	val encapsulate: ServerboundPlayerActionPacket
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<EncapsulatePlayerActionPhysicsGridPacket> = payloadType("plr_act_phys_grid")
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EncapsulatePlayerActionPhysicsGridPacket> =
			StreamCodec.composite(
				ByteBufCodecs.VAR_LONG, EncapsulatePlayerActionPhysicsGridPacket::id,
				ServerboundPlayerActionPacket.STREAM_CODEC, EncapsulatePlayerActionPhysicsGridPacket::encapsulate,
				::EncapsulatePlayerActionPhysicsGridPacket
			)

		fun handleServerboundPacket(data: EncapsulatePlayerActionPhysicsGridPacket, context: IPayloadContext) {
			context.enqueueWork {
				val grid = PhysicsGrid.Companion.serverGrids[data.id] ?: return@enqueueWork
				println("GRID ... $data, $grid ?")
				context.handle(data.encapsulate)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToServer(
			this.TYPE, this.STREAM_CODEC,
			this::handleServerboundPacket
		)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}