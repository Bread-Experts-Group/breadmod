package org.bread_experts_group.breadmod.util.buffer.render

import net.neoforged.neoforge.client.event.RenderLevelStageEvent

typealias BufferPassthrough = MutableList<Any>
typealias BufferLambda<T> = (event: T, args: BufferPassthrough) -> Boolean
typealias BufferEntry<D, T> = Triple<D?, T, BufferPassthrough>
typealias SingleBufferEntry<T> = Pair<T, BufferPassthrough>

private typealias RenderLambda = BufferLambda<RenderLevelStageEvent>

object RenderBuffer {
	private val renderBuffer: MutableList<BufferEntry<RenderLevelStageEvent.Stage, RenderLambda>> = mutableListOf()

	fun add(
		forStage: RenderLevelStageEvent.Stage? = null,
		entry: RenderLambda,
		passthrough: BufferPassthrough = mutableListOf()
	): Boolean = this.renderBuffer.add(Triple(forStage, entry, passthrough))

	fun handle(
		event: RenderLevelStageEvent
	) {
		this.renderBuffer.removeIf { (stage, entry, passthrough) ->
			if (stage == null || event.stage == stage) entry(event, passthrough)
			else false
		}
	}
}