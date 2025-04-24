package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.util.blockPhysicsGridLV
import org.bread_experts_group.breadmod.util.rayCast

class PushGridItem : Item(Item.Properties().stacksTo(1).rarity(Rarity.RARE)) {
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(usedHand)
		if (!level.isClientSide) return InteractionResultHolder.pass(stack) // TODO, proper sync of the grids
		val result = player.rayCast(50.0, blockPhysicsGridLV(level))
		if (result != null) {
			val (grid) = result.hit
			grid.velocity = grid.velocity.add(0.25, 0.0, 0.0)
		}
		return InteractionResultHolder.consume(stack)
	}
}