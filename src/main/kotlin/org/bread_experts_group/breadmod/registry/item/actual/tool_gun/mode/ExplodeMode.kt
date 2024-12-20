package org.bread_experts_group.breadmod.registry.item.actual.tool_gun.mode

import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level

class ExplodeMode : ToolGunMode("explode") {
	fun action(level : Level, player : Player) {
		level.explode(player, player.x, player.y, player.z, 20f, Level.ExplosionInteraction.MOB)
	}
}