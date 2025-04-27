package org.bread_experts_group.breadmod.network.serverbound

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.InteractionHand.OFF_HAND
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.CommonNeoForgeEventBus
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.registry.component.ModDataComponents
import org.bread_experts_group.breadmod.registry.item.ModItems
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.EmptyMode

class ToolGunModeChangePacket(private val id: ResourceLocation, private val index: Int) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ToolGunModeChangePacket> =
			CustomPacketPayload.Type(modLocation("tool_gun_packet"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ToolGunModeChangePacket> = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, ToolGunModeChangePacket::id,
			ByteBufCodecs.VAR_INT, ToolGunModeChangePacket::index,
			::ToolGunModeChangePacket
		)

		fun handleServerboundPacket(data: ToolGunModeChangePacket, context: IPayloadContext) {
			val player = context.player()
			val mainHand = player.getItemInHand(MAIN_HAND)
			val offHand = player.getItemInHand(OFF_HAND)
			val stack = if (mainHand.isEmpty) offHand else mainHand

			if (stack.`is`(ModItems.TOOL_GUN)) {
				val toolGunData = stack.getOrDefault(ModDataComponents.TOOL_GUN_DATA, ToolGunData.EMPTY)
				toolGunData.saveData()
				val newMode = CommonNeoForgeEventBus.toolGunModes[data.id] ?: EmptyMode
				stack.set(ModDataComponents.TOOL_GUN_DATA, ToolGunData(newMode, toolGunData.extraData, data.index))
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}