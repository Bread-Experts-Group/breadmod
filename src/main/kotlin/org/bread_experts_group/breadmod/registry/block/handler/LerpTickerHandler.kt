package org.bread_experts_group.breadmod.registry.block.handler

import net.neoforged.neoforge.capabilities.BlockCapability
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.render.LerpTicker
import org.bread_experts_group.breadmod.registry.block.actual.entity.BreadModBlockEntity

class LerpTickerHandler<E>(vararg parameters: Pair<E, LerpParams>) : LerpTicker<E>(*parameters) {
	companion object {
		@Suppress("UNCHECKED_CAST")
		fun <E> BreadModBlockEntity.getLerpTicker(): LerpTickerHandler<E> = this.getCapability(
			this@Companion.BLOCK_VOID
		) as LerpTickerHandler<E>

		val BLOCK_VOID: BlockCapability<LerpTickerHandler<*>, Void?> = BlockCapability.createVoid<LerpTickerHandler<*>>(
			modLocation("lerp_ticker"),
			LerpTickerHandler::class.java
		)
	}
}