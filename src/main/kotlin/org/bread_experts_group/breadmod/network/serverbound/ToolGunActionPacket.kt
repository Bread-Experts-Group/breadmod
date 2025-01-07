package org.bread_experts_group.breadmod.network.serverbound

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.InteractionHand.OFF_HAND
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.datagen.tool_gun.ToolGunModeDataLoader
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.ModItems

class ToolGunActionPacket(
	private val namespace : String,
	private val id : String
) : CustomPacketPayload {
	companion object {
		val TYPE : CustomPacketPayload.Type<ToolGunActionPacket> =
			CustomPacketPayload.Type(modLocation("tool_gun_packet"))
		val STREAM_CODEC : StreamCodec<RegistryFriendlyByteBuf, ToolGunActionPacket> = StreamCodec.composite(
			ByteBufCodecs.STRING_UTF8, ToolGunActionPacket::namespace,
			ByteBufCodecs.STRING_UTF8, ToolGunActionPacket::id,
			::ToolGunActionPacket
		)

		fun handleServerboundPacket(data : ToolGunActionPacket, context : IPayloadContext) {
			val player = context.player()
			val mainHand = player.getItemInHand(MAIN_HAND)
			val offHand = player.getItemInHand(OFF_HAND)
			val handStack = if (mainHand.isEmpty) offHand else mainHand

			if (handStack.`is`(ModItems.TOOL_GUN)) handStack.set(
				ModDataComponents.CURRENT_MODE,
				ToolGunModeDataLoader.modes[data.namespace]?.get(data.id)
				)
		}
	}

	override fun type() : CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}