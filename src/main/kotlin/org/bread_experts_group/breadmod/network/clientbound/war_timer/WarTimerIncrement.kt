package org.bread_experts_group.breadmod.network.clientbound.war_timer

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.gui.overlays.WarOverlay
import org.bread_experts_group.breadmod.registry.sound.ModSounds
import org.bread_experts_group.breadmod.client.render.localClient

internal data class WarTimerIncrement(
	private val increasing: Boolean,
	private val increaseTimer: Int
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<WarTimerIncrement> =
			CustomPacketPayload.Type(modLocation("war_timer_increment"))
		val STREAM_CODEC: StreamCodec<ByteBuf, WarTimerIncrement> = StreamCodec.composite(
			ByteBufCodecs.BOOL, WarTimerIncrement::increasing,
			ByteBufCodecs.INT, WarTimerIncrement::increaseTimer,
			::WarTimerIncrement
		)

		fun handleClientboundPacket(data: WarTimerIncrement, context: IPayloadContext) {
			context.enqueueWork {
				WarOverlay.isTimerIncreasing = data.increasing
				WarOverlay.increasingTimer = data.increaseTimer
				val player = localClient.player ?: return@enqueueWork
				player.playSound(ModSounds.WAR_TIMER_UP.get(), 0.7f, 1.0f)
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToClient(this.TYPE, this.STREAM_CODEC, this::handleClientboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}