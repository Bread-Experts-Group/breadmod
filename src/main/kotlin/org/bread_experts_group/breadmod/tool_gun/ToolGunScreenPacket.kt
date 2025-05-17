package org.bread_experts_group.breadmod.tool_gun

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.api.IToolGunMode
import org.bread_experts_group.breadmod.tool_gun.mode.EmptyMode
import org.bread_experts_group.breadmod.util.getStackInPlayerHand

class ToolGunScreenPacket(private val mode: IToolGunMode = EmptyMode()) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ToolGunScreenPacket> =
			CustomPacketPayload.Type(modLocation("screen_packet"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunScreenPacket> = StreamCodec.composite(
			IToolGunMode.STREAM_CODEC, ToolGunScreenPacket::mode,
			::ToolGunScreenPacket
		)

		fun handleServerboundPacket(data: ToolGunScreenPacket, context: IPayloadContext) {
			if (data.mode is EmptyMode)
				context.player().openMenu((getStackInPlayerHand(context.player()).item as ToolGunItem))
			else context.player().openMenu(data.mode)
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}