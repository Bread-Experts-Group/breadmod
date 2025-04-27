package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.shapes.CollisionContext
import org.bread_experts_group.breadmod.experimental.physics_grid.ClientPhysicsGrid
import org.bread_experts_group.breadmod.util.blockPhysicsGrid

class PushGridItem : Item(Item.Properties().stacksTo(1).rarity(Rarity.RARE)) {
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val stack = player.getItemInHand(usedHand)
		if (!level.isClientSide) return InteractionResultHolder.pass(stack) // TODO, proper sync of the grids
		val result = blockPhysicsGrid(
			{ it is ClientPhysicsGrid },
			player.eyePosition,
			player.calculateViewVector(player.xRot, player.yRot),
			false,
			CollisionContext.of(player)
		)
		if (result != null) {
			result.grid.velocity = result.grid.velocity.add(0.25, 0.0, 0.0)
		}
		return InteractionResultHolder.consume(stack)
	}
}