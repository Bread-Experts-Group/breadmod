package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.Rarity.UNCOMMON
import net.minecraft.world.item.context.UseOnContext
import org.bread_experts_group.breadmod.registry.entity.actual.Rocket

class RocketWrench : Item(Properties().rarity(UNCOMMON).stacksTo(1)) {
	private var posA: BlockPos? = null
	private var posB: BlockPos? = null

	override fun useOn(context: UseOnContext): InteractionResult {
		val clickedPos = context.clickedPos
		val player = context.player ?: return super.useOn(context)
		val level = context.level
		if (clickedPos is BlockPos.MutableBlockPos) return super.useOn(context)
		if (this.posA == null) {
			this.posA = clickedPos
			player.sendSystemMessage(Component.literal("posA = ${this.posA}"))
			return InteractionResult.sidedSuccess(level.isClientSide)
		}
		if (this.posB == null) {
			this.posB = clickedPos
			player.sendSystemMessage(Component.literal("posB = ${this.posB}"))
			return InteractionResult.sidedSuccess(level.isClientSide)
		}
		val entity = Rocket(level, this.posA!!, this.posB!!)
		entity.setPos(clickedPos.center.add(0.0, 2.0, 0.0))
		level.addFreshEntity(entity)
		this.posA = null
		this.posB = null
		return super.useOn(context)
	}
}