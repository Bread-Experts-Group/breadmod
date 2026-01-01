package org.bread_experts_group.breadmod.experimental.physics_grid

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.ServerMicroLevel
import org.bread_experts_group.breadmod.registry.item.IMouseItem

class BulkBlockItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IMouseItem {
	private var posA: BlockPos = BlockPos.ZERO
	private var posB: BlockPos = BlockPos.ZERO

	override fun useOn(context: UseOnContext): InteractionResult {
		if (!context.level.isClientSide) {
			context.player?.sendSystemMessage(
				Component.literal(
					ServerMicroLevel.getNameSpaceAndNameHuffmanSD(
						(context.level as ServerLevel).server
					).toString()
				)
			)
			ServerMicroLevel.computeNameSpaceAndNameHuffmanSD(
				(context.level as ServerLevel).server
			)
			context.player?.sendSystemMessage(
				Component.literal(
					ServerMicroLevel.getNameSpaceAndNameHuffmanSD(
						(context.level as ServerLevel).server
					).toString()
				)
			)
		}

		if (context.clickedPos is BlockPos.MutableBlockPos) return super.useOn(context)
		if (this.posA == BlockPos.ZERO) {
			this.posA = context.clickedPos
			context.player?.sendSystemMessage(Component.literal("A = ${this.posA}"))
			return InteractionResult.sidedSuccess(context.level.isClientSide)
		}
		if (this.posB == BlockPos.ZERO) {
			this.posB = context.clickedPos
			context.player?.sendSystemMessage(Component.literal("B = ${this.posB}"))
			return InteractionResult.sidedSuccess(context.level.isClientSide)
		}
		if (this.posA != BlockPos.ZERO && this.posB != BlockPos.ZERO) {
			PhysicsGrid.add(this.posA, this.posB, context)
		}
		this.posA = BlockPos.ZERO
		this.posB = BlockPos.ZERO
		return super.useOn(context)
	}

	override fun onMouseScroll(scrollingEvent: InputEvent.MouseScrollingEvent, heldStack: ItemStack, player: Player) {
		if (player.isCrouching) {
			PhysicsGrid.grids.clear()
			PacketDistributor.sendToServer(ClearGridPacket())
			scrollingEvent.isCanceled = true
		}
	}
}