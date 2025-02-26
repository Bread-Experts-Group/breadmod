package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.DOWN
import net.minecraft.core.Direction.EAST
import net.minecraft.core.Direction.NORTH
import net.minecraft.core.Direction.SOUTH
import net.minecraft.core.Direction.UP
import net.minecraft.core.Direction.WEST
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type
import net.minecraft.world.InteractionHand.MAIN_HAND
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.registry.block.ModBlocks.asBlock
import org.bread_experts_group.breadmod.registry.block.actual.entity.ItemInWorldBlockEntity

class PlaceItemInWorldPacket(
	private val pos: BlockPos,
	private val direction: Direction
) : CustomPacketPayload {
	companion object {
		val TYPE: Type<PlaceItemInWorldPacket> =
			Type(modLocation("place_item_packet"))
		val STREAM_CODEC: StreamCodec<ByteBuf, PlaceItemInWorldPacket> = StreamCodec.composite(
			BlockPos.STREAM_CODEC, PlaceItemInWorldPacket::pos,
			Direction.STREAM_CODEC, PlaceItemInWorldPacket::direction,
			::PlaceItemInWorldPacket
		)

		fun handleServerboundPacket(data: PlaceItemInWorldPacket, context: IPayloadContext) {
			val player = context.player()
			val level = player.level()
			val stack = player.getItemInHand(MAIN_HAND)
			val blockState = level.getBlockState(data.pos)
			val pos = if (!blockState.`is`(ModBlocks.ITEM_IN_WORLD_BLOCK.asBlock())) when (data.direction) {
				UP    -> data.pos.above()
				DOWN  -> data.pos.below()
				NORTH -> data.pos.north()
				SOUTH -> data.pos.south()
				EAST  -> data.pos.east()
				WEST  -> data.pos.west()
			} else data.pos
			val checkState = level.getBlockState(pos)

			if (!stack.isEmpty) {
				if (!blockState.`is`(ModBlocks.ITEM_IN_WORLD_BLOCK.asBlock()) && checkState.`is`(Blocks.AIR)) {
					level.setBlockAndUpdate(
						pos,
						ModBlocks.ITEM_IN_WORLD_BLOCK.asBlock().defaultBlockState().setValue(
							BlockStateProperties.FACING,
							data.direction
						)
					)
					val entity = level.getBlockEntity(pos) as? ItemInWorldBlockEntity ?: return
					entity.setItem(0, if (player.isCreative) stack.copy() else stack.copyAndClear())
				} else {
					val entity = level.getBlockEntity(pos) as? ItemInWorldBlockEntity ?: return
					val slotIndex = entity.itemHandler.filledSlots
					entity.setItem(slotIndex, if (player.isCreative) stack.copy() else stack.copyAndClear())
				}
				level.sendBlockUpdated(pos, blockState, blockState, 3)
			}
		}
	}

	override fun type(): Type<out CustomPacketPayload> = Companion.TYPE
}