package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.context.UseOnContext
import net.neoforged.neoforge.network.PacketDistributor
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.experimental.physics_grid.ServerPhysicsGrid
import org.bread_experts_group.breadmod.network.clientbound.physics_grid.ClientPhysicsGridPacket
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
		val level = context.level
		val serverPlayer = context.player as? ServerPlayer ?: return super.useOn(context)
		if (!level.isClientSide) {
			LogManager.getLogger().info("creating physics grid")
			ServerPhysicsGrid(level as ServerLevel, this.posA!!, this.posB!!)
				.setPos(serverPlayer.position())
			PacketDistributor.sendToPlayer(serverPlayer, ClientPhysicsGridPacket(this.posA!!, this.posB!!))
		}
		this.posA = null
		this.posB = null
		context.player?.sendSystemMessage(Component.literal("Created physics grid."))
		return super.useOn(context)
	}
}