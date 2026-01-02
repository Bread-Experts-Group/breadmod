package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.Holder
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEventDispatcher
import net.minecraft.world.phys.Vec3

class MicroLevelGameEventDispatcher(level: ServerMicroLevel) : GameEventDispatcher(level) {
	override fun post(gameEvent: Holder<GameEvent>, pos: Vec3, context: GameEvent.Context) {
		println("$gameEvent, $pos, $context")
	}
}