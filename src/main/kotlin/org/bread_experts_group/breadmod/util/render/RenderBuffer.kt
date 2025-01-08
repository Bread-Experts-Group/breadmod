package org.bread_experts_group.breadmod.util.render

import net.neoforged.neoforge.client.event.RenderLevelStageEvent

typealias RenderBufferPassthrough = MutableList<Any>
typealias RenderBufferLambda = (event: RenderLevelStageEvent, args: RenderBufferPassthrough) -> Boolean
typealias RenderBufferEntry = Triple<RenderLevelStageEvent.Stage?, RenderBufferLambda, RenderBufferPassthrough>

object RenderBuffer {
	private val renderBuffer: MutableList<RenderBufferEntry> = mutableListOf()

	fun add(
		forStage: RenderLevelStageEvent.Stage? = null,
		entry: RenderBufferLambda,
		passthrough: RenderBufferPassthrough = mutableListOf()
	): Boolean = this.renderBuffer.add(Triple(forStage, entry, passthrough))

	fun handle(
		event: RenderLevelStageEvent
	): Unit = this.renderBuffer.forEach { (stage, entry, passthrough) ->
		if (stage == null || event.stage == stage) entry(event, passthrough)
	}
}