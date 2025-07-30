package org.bread_experts_group.breadmod.network.serverbound

import net.minecraft.core.BlockPos
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.actual.entity.handler.ComputerHandler

class ComputerKeystrokePacket(private val monitorPos: BlockPos, private val keyCode: Int) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ComputerKeystrokePacket> =
			CustomPacketPayload.Type(modLocation("computer_keystroke_packet"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ComputerKeystrokePacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, ComputerKeystrokePacket::monitorPos,
			ByteBufCodecs.INT, ComputerKeystrokePacket::keyCode,
			::ComputerKeystrokePacket
		)

		fun handleServerboundPacket(data: ComputerKeystrokePacket, context: IPayloadContext) {
			context.enqueueWork {
				val level = context.player().level()
				val monitorEntity = level.getBlockEntity(data.monitorPos) as? BreadModBlockEntity ?: return@enqueueWork
				val monitorComputer = monitorEntity.getCapability(ComputerHandler.BLOCK_VOID)
				monitorComputer.computer.keyboard.write(data.keyCode.toUByte())
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}