package org.bread_experts_group.breadmod.experimental.physics_grid

import com.google.common.base.Objects
import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod

class ClearGridPacket : CustomPacketPayload {
	companion object {
		private val TYPE: CustomPacketPayload.Type<ClearGridPacket> =
			CustomPacketPayload.Type(BreadMod.Companion.modLocation("clear_grid"))
		val STREAM_CODEC: StreamCodec<ByteBuf, ClearGridPacket> = StreamCodec.unit(ClearGridPacket())

		fun handleServerbound(context: IPayloadContext) {
			context.enqueueWork {
				PhysicsGrid.serverGrids.clear()
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC) { _, context -> this.handleServerbound(context) }
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload?> = Companion.TYPE

	override fun equals(other: Any?): Boolean = other is ClearGridPacket

	override fun hashCode(): Int = Objects.hashCode(Companion.TYPE, Companion.STREAM_CODEC)
}