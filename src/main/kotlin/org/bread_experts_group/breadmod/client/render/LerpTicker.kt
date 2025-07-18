package org.bread_experts_group.breadmod.client.render

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import java.lang.Math.clamp

interface LerpTicker {
	val lerpParams: Array<LerpParams>

	fun getLerpedValue(index: Int, partialTick: Float): Float {
		val start = this.lerpParams[index].previous
		val end = this.lerpParams[index].position
		return if (partialTick == 1f) end else start + partialTick * (end - start)
	}

	fun getRawValue(index: Int): Float = this.lerpParams[index].position

	fun tickAllPositions() {
		this.lerpParams.forEach {
			if (it.isHandledManually) return@forEach
			it.previous = it.position
			it.setClampedPos(it.incrementAmount)
		}
	}

	fun tickIndex(index: Int, customAmount: Float? = null) {
		val params = this.lerpParams[index]
		params.previous = params.position
		params.setClampedPos(customAmount ?: params.incrementAmount)
	}

	fun setParamPosition(index: Int, position: Float) {
		val params = this.lerpParams[index]
		params.previous = position - params.incrementAmount
		params.setClampedPos(position)
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
		var isHandledManually: Boolean = false,
		var clampMin: Float = Float.MIN_VALUE,
		var clampMax: Float = Float.MAX_VALUE
	) {
		fun tick() {
			this.previous = this.position
			this.setClampedPos(this.incrementAmount)
		}

		fun setClampedPos(value: Float) {
			this.position = clamp(this.position + value, this.clampMin, this.clampMax)
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