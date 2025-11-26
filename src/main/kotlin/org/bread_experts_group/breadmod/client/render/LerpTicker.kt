package org.bread_experts_group.breadmod.client.render

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import java.lang.Math.clamp

open class LerpTicker<E>(
	vararg parameters: Pair<E, LerpParams>
) {
	private val lerpParams: MutableMap<E, LerpParams> = mutableMapOf(*parameters)

	fun getLerpedValue(label: E, partialTick: Float): Float {
		val parameters = this.lerpParams.getValue(label)
		return if (partialTick == 1f) parameters.position
		else parameters.previous + partialTick * (parameters.position - parameters.previous)
	}

	fun getRawValue(label: E): Float = this.lerpParams.getValue(label).position

	/**
	 * Returns the lerped value for this [label] while it's above 0f, else it will return the raw value
	 * to prevent jittering due to [partialTick].
	 *
	 * @param checker used for returning the lerped or raw value.
	 * Example would be checking delta on another label then returning the lerped value if it's above 0,
	 * defaults to using [label].
	 */
	fun getLerpedOrRawValue(label: E, partialTick: Float, checker: E = label): Float =
		if (this.getRawValue(checker) > 0f) this.getLerpedValue(label, partialTick) else
			this.getRawValue(label)

	fun getLerpedIfElseRaw(label: E, partialTick: Float, checker: E = label, condition: () -> Boolean): Float =
		if (condition()) this.getLerpedOrRawValue(label, partialTick, checker) else this.getRawValue(label)

	fun tickAllPositions() {
		this.lerpParams.forEach { (_, parameters) ->
			if (parameters.isHandledManually) return@forEach
			parameters.previous = parameters.position
			parameters.setClampedPos(parameters.incrementAmount)
		}
	}

	fun tickIndex(label: E, customAmount: Float? = null) {
		val params = this.lerpParams.getValue(label)
		params.previous = params.position
		params.setClampedPos(customAmount ?: params.incrementAmount)
	}

	fun setParamPosition(label: E, position: Float) {
		val params = this.lerpParams.getValue(label)
		params.previous = position - params.incrementAmount
		params.setClampedPos(position)
	}

	/**
	 * LerpParams#previous is set automatically before [run] is invoked.
	 */
	fun tickCustom(label: E, run: (LerpParams) -> Unit) {
		val params = this.lerpParams.getValue(label)
		params.previous = params.position
		run(params)
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
	abstract class BEWLR<E>(
		vararg parameters: Pair<E, LerpParams>
	) : LerpTicker<E>(*parameters) {
		private fun getPartialTick(): Float = localClient.timer.getGameTimeDeltaPartialTick(false)

		fun getLerpedValue(label: E): Float = this.getLerpedValue(label, this.getPartialTick())

		/**
		 * @see org.bread_experts_group.breadmod.client.render.LerpTicker.getLerpedOrRawValue
		 */
		fun getLerpedOrRawValue(label: E, checker: E = label): Float =
			this.getLerpedOrRawValue(label, this.getPartialTick(), checker)

		/**
		 * @see org.bread_experts_group.breadmod.client.render.LerpTicker.getLerpedIfElseRaw
		 */
		fun getLerpedIfElseRaw(label: E, checker: E = label, condition: () -> Boolean): Float =
			this.getLerpedIfElseRaw(label, this.getPartialTick(), checker, condition)

		abstract fun tick()
	}
}