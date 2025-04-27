package org.bread_experts_group.breadmod.network.serverbound

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.InteractionHand.OFF_HAND
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.network.BreadModCodecs
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.ModItems

class ToolGunDataSyncPacket(private val data: ToolGunData) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ToolGunDataSyncPacket> =
			CustomPacketPayload.Type(modLocation("tool_gun_data_packet"))
		val STREAM_CODEC: StreamCodec<FriendlyByteBuf, ToolGunDataSyncPacket> = StreamCodec.composite(
			BreadModCodecs.TOOL_GUN_STREAM_CODEC, ToolGunDataSyncPacket::data,
			::ToolGunDataSyncPacket
		)

		fun handleServerboundPacket(data: ToolGunDataSyncPacket, context: IPayloadContext) {
			val player = context.player()
			val mainHand = player.getItemInHand(MAIN_HAND)
			val offHand = player.getItemInHand(OFF_HAND)
			val stack = if (mainHand.isEmpty) offHand else mainHand

			if (stack.`is`(ModItems.TOOL_GUN)) stack.set(ModDataComponents.TOOL_GUN_DATA, data.data)
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}