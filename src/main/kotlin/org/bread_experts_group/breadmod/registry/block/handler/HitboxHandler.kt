package org.bread_experts_group.breadmod.registry.block.handler

import net.minecraft.world.phys.Vec3
import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity
import org.bread_experts_group.breadmod.util.Hitbox

class HitboxHandler(
	vararg val hitboxes: Hitbox
) : ParentedHandler<BreadModBlockEntity> {
	companion object {
		val hitboxes: MutableMap<Vec3, Hitbox> = mutableMapOf()

		fun BreadModBlockEntity.getHitboxHandler(): HitboxHandler =
			this.getCapability(this@Companion.BLOCK_VOID) as HitboxHandler

		val BLOCK_VOID: BlockCapability<HitboxHandler, Void?> = BlockCapability.createVoid(
			modLocation("hitbox_handler"),
			HitboxHandler::class.java
		)
	}

	init {
		this.hitboxes.forEach { hitbox ->
			Companion.hitboxes[hitbox.pos] = hitbox
		}
	}

	fun discard() {
		this.hitboxes.forEach { Companion.hitboxes.remove(it.pos) }
	}

	override lateinit var parent: BreadModBlockEntity
	override val stateListeners: MutableList<() -> Unit> = mutableListOf()
}