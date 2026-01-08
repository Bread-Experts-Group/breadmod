package org.bread_experts_group.breadmod.registry.item.actual

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.experimental.physics_grid.GridPacket
import org.bread_experts_group.breadmod.experimental.physics_grid.PhysicsGrid

class PushGridItem : Item(Item.Properties().stacksTo(1).rarity(Rarity.RARE)) {
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		val grid = PhysicsGrid.getClosestGrid(player) ?: return super.use(level, player, usedHand)
		val lookingDirection = player.calculateViewVector(player.xRot, player.yRot)
		if (level.isClientSide) GridPacket.relayGridMovement(lookingDirection, grid.id)
		return InteractionResultHolder.sidedSuccess(player.getItemInHand(usedHand), level.isClientSide)
	}
}