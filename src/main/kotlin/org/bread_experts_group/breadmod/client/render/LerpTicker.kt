package org.bread_experts_group.breadmod.client.render

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.util.Mth

interface LerpTicker {
	val lerpParams: Array<LerpParams>

	fun getLerpedValue(index: Int, partialTick: Float): Float =
		if (partialTick == 1f) this.lerpParams[index].position
		else Mth.lerp(partialTick, this.lerpParams[index].previous, this.lerpParams[index].position)

	fun getRawValue(index: Int): Float = this.lerpParams[index].position

	fun tickAllPositions() {
		this.lerpParams.forEach {
			if (it.isHandledManually) return@forEach
			it.previous = it.position
			it.position += it.incrementAmount
		}
	}

	fun tickIndex(index: Int, customAmount: Float? = null) {
		val params = this.lerpParams[index]
		params.previous = params.position
		params.position += customAmount ?: params.incrementAmount
	}

	fun setParamPosition(index: Int, position: Float) {
		val params = this.lerpParams[index]
		params.previous = position - params.incrementAmount
		params.position = position
	}

	/**
	 * LerpParams#previous is set automatically before [run] is invoked.
	 */
	fun tickCustom(index: Int, run: (LerpParams) -> Unit) {
		val params = this.lerpParams[index]
		params.previous = params.position
		run.invoke(params)
	}

	data class LerpParams(
		/**
		 * this is set to [position] every tick, updates before [position].
		 */
		var previous: Float = 0f,
		/**
		 * updates after [previous] every tick.
		 */
		var position: Float = 0f,
		/**
		 * the amount that [position] is incremented by every tick.
		 */
		var incrementAmount: Float = 1f,
		/**
		 * if this is true, this param must be ticked manually through tickCustom or tickIndex.
		 */
		var isHandledManually: Boolean = false
	) {
		fun tick() {
			this.previous = this.position
			this.position += this.incrementAmount
		}
	}

	/**
	 * [BlockEntityWithoutLevelRenderer] specific interface for ticking [lerpParams].
	 */
	interface BEWLR : LerpTicker {
		fun getLerpedValue(index: Int): Float =
			this.getLerpedValue(index, localClient.timer.getGameTimeDeltaPartialTick(false))

		fun tick()
	}
}