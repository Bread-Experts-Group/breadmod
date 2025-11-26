package org.bread_experts_group.breadmod.registry.item

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent

interface IEntityInteractingItem {
	fun onInteractWithEntity(
		event: PlayerInteractEvent.EntityInteract,
		player: Player,
		target: Entity,
		level: Level,
		usedHand: InteractionHand,
		pos: BlockPos,
		stack: ItemStack
	)
}