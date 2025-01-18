package org.bread_experts_group.breadmod.network.serverbound

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.InteractionHand.OFF_HAND
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode

class ToolGunModeChangePacket(private val id: ResourceLocation) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ToolGunModeChangePacket> =
			CustomPacketPayload.Type(modLocation("tool_gun_packet"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunModeChangePacket> = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ToolGunModeChangePacket::id,
			::ToolGunModeChangePacket
		)

		fun handleServerboundPacket(data: ToolGunModeChangePacket, context: IPayloadContext) {
			val player = context.player()
			val mainHand = player.getItemInHand(MAIN_HAND)
			val offHand = player.getItemInHand(OFF_HAND)
			val handStack = if (mainHand.isEmpty) offHand else mainHand

			if (handStack.`is`(ModItems.TOOL_GUN)) handStack.set(
				ModDataComponents.TOOL_GUN_DATA,
				CommonNeoForgeEventBus.toolGunModes[data.id] ?: EmptyMode()
			)
		}
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}