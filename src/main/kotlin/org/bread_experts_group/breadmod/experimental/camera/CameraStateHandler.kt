package org.bread_experts_group.breadmod.experimental.camera

import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.registry.block.handler.DiscardableHandler
import org.bread_experts_group.breadmod.registry.block.handler.ParentedHandler
import java.util.UUID

class CameraStateHandler(
	val id: UUID
) : ParentedHandler<BreadModBlockEntity>, DiscardableHandler {
	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
	override lateinit var parent: BreadModBlockEntity

	override fun discard() {
		val level = this.parent.level ?: throw NullPointerException("Level should not be null.")
		if (level.isClientSide) {
			CameraBlockRenderer.textures.getValue(this.id).close()
			CameraBlockRenderer.textures.remove(this.id)
		}
	}

	companion object {
		val BLOCK_VOID: BlockCapability<CameraStateHandler, Void?> = BlockCapability.createVoid(
			modLocation("camera_state_handler"),
			CameraStateHandler::class.java
		)
	}
}