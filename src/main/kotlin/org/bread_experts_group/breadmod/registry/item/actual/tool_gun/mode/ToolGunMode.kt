package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

// todo hash out finer details before actually trying to write this up
abstract class ToolGunMode {
	abstract fun action(level : Level, player : Player, stack : ItemStack)
}