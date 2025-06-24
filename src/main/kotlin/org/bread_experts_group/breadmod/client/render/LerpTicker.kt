package org.bread_experts_group.breadmod.client.render

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.util.Mth

interface LerpTicker {
	val lerpParams: Array<LerpParams>

	/**
	 * populate the [partialTick] parameter if your calling this inside of a Screen or BER.
	 */
	fun getLerpedValue(index: Int, partialTick: Float): Float =
		if (partialTick == 1f) this.lerpParams[index].position
		else Mth.lerp(partialTick, this.lerpParams[index].previous, this.lerpParams[index].position)

	fun getRawValue(index: Int): Float = this.lerpParams[index].position

	fun tickAllPositions() {
		this.lerpParams.forEach {
			it.previous = it.position
			it.position += it.amount
		}
	}

	fun tickPositionIndex(index: Int, customAmount: Float? = null) {
		val params = this.lerpParams[index]
		params.previous = params.position
		params.position += customAmount ?: params.amount
	}

	fun setParamsAmount(index: Int, amount: Float) {
		this.lerpParams[index].amount = amount
	}

	fun setParamPosition(index: Int, position: Float) {
		val params = this.lerpParams[index]
		params.previous = position - params.amount
		params.position = position
	}

	fun getLerpParam(index: Int): LerpParams = this.lerpParams[index]

	data class LerpParams(
		var previous: Float = 0f,
		var position: Float = 0f,
		var amount: Float = 1f,
		var isClamped: Boolean = false
	)

	/**
	 * [BlockEntityWithoutLevelRenderer] specific interface for ticking [lerpParams].
	 */
	interface BEWLR : LerpTicker {
		fun getLerpedValue(index: Int): Float =
			this.getLerpedValue(index, localClient.timer.getGameTimeDeltaPartialTick(false))

		fun tick()
	}
}