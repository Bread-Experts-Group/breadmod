package org.bread_experts_group.breadmod.experimental.physics_grid.backend

import net.minecraft.core.Holder
import net.minecraft.world.level.gameevent.GameEvent
import net.minecraft.world.level.gameevent.GameEventDispatcher
import net.minecraft.world.level.gameevent.GameEventListener
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadmod.experimental.physics_grid.backend.server.ServerMicroLevel

class MicroLevelGameEventDispatcher(private val parent: ServerMicroLevel) : GameEventDispatcher(parent) {
	override fun post(gameEvent: Holder<GameEvent>, pos: Vec3, context: GameEvent.Context) {
		println("***2 $gameEvent, $pos, $context")
		this.parent
			.getChunk(0, 0)
			.getListenerRegistry(0)
			.visitInRangeListeners(gameEvent, pos, context) { listener, posS ->
				if (listener.deliveryMode == GameEventListener.DeliveryMode.BY_DISTANCE) {
					println("by distance $listener, $posS")
				} else {
					println("unspecified $listener, $posS")
					listener.handleGameEvent(this.parent, gameEvent, context, pos)
				}
			}
	}
}