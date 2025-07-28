package org.bread_experts_group.breadmod.network.serverbound

// todo uncomment in CommonModEventBus after fixing
//class ComputerKeystrokePacket(private val monitorPos: BlockPos, private val keyCode: Int) : CustomPacketPayload {
//	companion object {
//		val TYPE: CustomPacketPayload.Type<ComputerKeystrokePacket> =
//			CustomPacketPayload.Type(modLocation("computer_keystroke_packet"))
//		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, ComputerKeystrokePacket> = StreamCodec.composite(
//			BlockPos.STREAM_CODEC, ComputerKeystrokePacket::monitorPos,
//			ByteBufCodecs.INT, ComputerKeystrokePacket::keyCode,
//			::ComputerKeystrokePacket
//		)
//
//		fun handleServerboundPacket(data: ComputerKeystrokePacket, context: IPayloadContext) {
//			context.enqueueWork {
//				val level = context.player().level()
//				val monitorEntity = level.getBlockEntity(data.monitorPos) as? MonitorBlockEntity ?: return@enqueueWork
//				monitorEntity.computer.keyboard.write(data.keyCode.toUByte())
//				val monitorState = level.getBlockState(data.monitorPos)
//				monitorEntity.setChanged()
//				level.setBlockAndUpdate(data.monitorPos, monitorState)
//			}
//		}
//
//		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
//			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
//	}
//
//	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
//}