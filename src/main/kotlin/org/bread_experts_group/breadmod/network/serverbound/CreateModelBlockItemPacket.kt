package org.bread_experts_group.breadmod.network.serverbound

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.network.handling.IPayloadContext
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.ModDataComponents
import org.bread_experts_group.breadmod.registry.block.ModBlocks
import org.bread_experts_group.breadmod.tool_gun.Model
import org.bread_experts_group.breadmod.util.block
import org.bread_experts_group.breadmod.util.listOf

class CreateModelBlockItemPacket(val blocks: List<Model>) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<CreateModelBlockItemPacket> =
			CustomPacketPayload.Type(modLocation("place_model"))
		val STREAM_CODEC: StreamCodec<ByteBuf, CreateModelBlockItemPacket> = StreamCodec.composite(
			Model.STREAM_CODEC.listOf(), CreateModelBlockItemPacket::blocks,
			::CreateModelBlockItemPacket
		)

		fun handleServerboundPacket(data: CreateModelBlockItemPacket, context: IPayloadContext) {
			val player = context.player()
			val level = player.level()
			val stack = ItemStack(ModBlocks.MODEL_BLOCK.block)
			stack.set(ModDataComponents.MODEL_DATA, data.blocks)
			level.addFreshEntity(ItemEntity(level, player.x, player.y + player.eyeHeight, player.z, stack))
		}

		fun register(registrar: PayloadRegistrar): PayloadRegistrar =
			registrar.playToServer(this.TYPE, this.STREAM_CODEC, this::handleServerboundPacket)
	}

	override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = Companion.TYPE
}