package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.HitboxHandler
import org.bread_experts_group.breadmod.util.hitbox
import org.bread_experts_group.breadmod.util.rayCast
import java.util.Objects

class HitboxPacket : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<HitboxPacket> =
			CustomPacketPayload.Type(modLocation("hitbox_packet"))
		val STREAM_CODEC: StreamCodec<ByteBuf, HitboxPacket> = StreamCodec.unit(HitboxPacket())
		fun handleServerboundPacket(data: HitboxPacket, context: IPayloadContext) {
			context.enqueueWork {
				val player = context.player()
				val level = player.level()
				player.rayCast(10.0, hitbox())?.let { result ->
					val state = level.getBlockState(result.blockPosition)
					val entity = level.getBlockEntity(result.hit.originBlockPos) as? BreadModBlockEntity
					val blockPos = result.hit.originBlockPos
					if (entity == null) {
						HitboxHandler.hitboxes.remove(result.hit.pos)
						return@enqueueWork
					}
					result.hit.onHitServer(level as ServerLevel, blockPos, state, player, entity)
					result.hit.onHitCommon(level, blockPos, state, player, entity)
					player.swing(player.usedItemHand, true)
				}
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE

	override fun hashCode(): Int = Objects.hash(Companion.TYPE, Companion.STREAM_CODEC)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		return true
	}
}