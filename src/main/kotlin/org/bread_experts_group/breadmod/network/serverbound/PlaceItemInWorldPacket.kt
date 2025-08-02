package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.handler.SlotQueueHandler

class PlaceItemInWorldPacket(
	private val pos: BlockPos,
	private val direction: Direction
) : CustomPacketPayload {
	companion object {
		val TYPE: Type<PlaceItemInWorldPacket> = Type(modLocation("place_item_packet"))
		val STREAM_CODEC: StreamCodec<ByteBuf, PlaceItemInWorldPacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PlaceItemInWorldPacket::pos,
			Direction.STREAM_CODEC, PlaceItemInWorldPacket::direction,
			::PlaceItemInWorldPacket
		)

		fun handleServerboundPacket(data: PlaceItemInWorldPacket, context: IPayloadContext) {
			val player = context.player()
			val level = player.level() as ServerLevel
			val stack = player.getItemInHand(InteractionHand.MAIN_HAND)
			val blockState = level.getBlockState(data.pos)
			val pos = if (!blockState.`is`(ModBlocks.ITEM_IN_WORLD_BLOCK.get())) when (data.direction) {
				Direction.UP -> data.pos.above()
				Direction.DOWN -> data.pos.below()
				Direction.NORTH -> data.pos.north()
				Direction.SOUTH -> data.pos.south()
				Direction.EAST -> data.pos.east()
				Direction.WEST -> data.pos.west()
			} else data.pos
			val checkState = level.getBlockState(pos)
			if (!stack.isEmpty) {
				if (!blockState.`is`(ModBlocks.ITEM_IN_WORLD_BLOCK.get()) && checkState.`is`(Blocks.AIR)) {
					level.setBlockAndUpdate(
						pos,
						ModBlocks.ITEM_IN_WORLD_BLOCK.get().defaultBlockState().setValue(
							BlockStateProperties.FACING,
							data.direction
						)
					)
				}
				val queue = level.getCapability(SlotQueueHandler.BLOCK_VOID, pos)
				if (queue != null && queue.size < 4) {
					queue.append(if (player.isCreative) stack.copy() else stack.copyAndClear())
					level.sendBlockUpdated(pos, blockState, blockState, 3)
				}
			}
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}