package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode.ExplodeMode

class ToolGunActionPacket : CustomPacketPayload {
	companion object {
		val TYPE : CustomPacketPayload.Type<ToolGunActionPacket> =
			CustomPacketPayload.Type(modLocation("tool_gun_packet"))
		val STREAM_CODEC : StreamCodec<ByteBuf, ToolGunActionPacket> = StreamCodec.unit(ToolGunActionPacket())

		fun handleServerboundPacket(data : ToolGunActionPacket, context : IPayloadContext) {
			val player = context.player()
			val level = player.level()
			val stack = player.getItemInHand(player.usedItemHand)
			val mode = ExplodeMode()

			mode.action(level, player, stack)
		}
	}

	override fun type() : CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}