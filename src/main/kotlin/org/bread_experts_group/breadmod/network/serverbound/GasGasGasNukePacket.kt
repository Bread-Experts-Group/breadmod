package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.world.entity.ai.attributes.Attributes
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.ModDamageType
import org.bread_experts_group.breadmod.registry.item.actual.OilDrumItem
import org.bread_experts_group.breadmod.util.BreadModExplosion

class GasGasGasNukePacket(
	private val doNothing: Boolean = false
) : CustomPacketPayload {
	companion object {
		val TYPE: Type<GasGasGasNukePacket> =
			Type(modLocation("gas_gas_gas_nuke"))
		val STREAM_CODEC: StreamCodec<ByteBuf, GasGasGasNukePacket> = StreamCodec.composite(
			ByteBufCodecs.BOOL, GasGasGasNukePacket::doNothing,
			::GasGasGasNukePacket
		)

		fun handleServerboundPacket(data: GasGasGasNukePacket, context: IPayloadContext) {
			if (data.doNothing) return
			val player = context.player()
			val movementSpeed = player.attributes.getInstance(Attributes.MOVEMENT_SPEED)!!
			if (movementSpeed.hasModifier(OilDrumItem.DRUM_SPEED_ID)) {
				movementSpeed.removeModifier(OilDrumItem.DRUM_SPEED_ID)
				player.hurt(ModDamageType.GAS_GAS_GAS.source(player.level()), 20000f)
				BreadModExplosion
					.calculate(player.level(), player.position(), 200f, 100000)
					.explode(null)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar = registrar.playToServer(
			this.TYPE, this.STREAM_CODEC,
			this::handleServerboundPacket
		)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}