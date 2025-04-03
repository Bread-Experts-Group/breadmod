package org.bread_experts_group.breadmod.registry.item.actual

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.client.event.InputEvent.MouseButton.Post
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadmod.network.serverbound.PhysicsGridRequestPacket
import org.bread_experts_group.breadmod.registry.item.IMouseItem

class BulkBlockItem : Item(Properties().stacksTo(1).rarity(Rarity.UNCOMMON)), IMouseItem {
	private var posA: BlockPos? = null
	private var posB: BlockPos? = null

	override fun useOn(context: UseOnContext): InteractionResult {
		if (context.clickedPos is BlockPos.MutableBlockPos) return super.useOn(context)
		if (this.posA == null) {
			this.posA = context.clickedPos
			context.player?.sendSystemMessage(Component.literal("A = ${this.posA}"))
			return InteractionResult.sidedSuccess(context.level.isClientSide)
		}
		if (this.posB == null) {
			this.posB = context.clickedPos
			context.player?.sendSystemMessage(Component.literal("B = ${this.posB}"))
			return InteractionResult.sidedSuccess(context.level.isClientSide)
		}
		this.posA = null
		this.posB = null
		context.player?.sendSystemMessage(Component.literal("Reset position"))
		return super.useOn(context)
	}

	override fun onMouseInputPost(mouseEvent: Post, heldStack: ItemStack, player: Player) {
		if (
			mouseEvent.action == InputConstants.PRESS &&
			mouseEvent.button == InputConstants.MOUSE_BUTTON_MIDDLE &&
			this.posA != null && this.posB != null
		) {
			PacketDistributor.sendToServer(
				PhysicsGridRequestPacket(
					player.position().toVector3f(),
					this.posA!!, this.posB!!
				)
			)
			player.sendSystemMessage(Component.literal("Physics grid requested"))
		}
	}
}