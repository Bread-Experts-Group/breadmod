package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class EmptyMode : ToolGunMode() {
	override fun action(level: Level, player: Player, stack: ItemStack) {
		player.sendSystemMessage(Component.literal("If you see this then the current mode is EmptyMode."))
	}
}