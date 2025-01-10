package org.bread_experts_group.breadmod.util.buffer.chunk

import net.neoforged.neoforge.event.level.ChunkEvent
import org.bread_experts_group.breadmod.util.buffer.render.BufferLambda
import org.bread_experts_group.breadmod.util.buffer.render.BufferPassthrough
import org.bread_experts_group.breadmod.util.buffer.render.SingleBufferEntry

private typealias ChunkLoadLambda = BufferLambda<ChunkEvent.Load>
private typealias ChunkUnloadLambda = BufferLambda<ChunkEvent.Unload>

object ChunkBuffer {
	private val loadBuffer: MutableList<SingleBufferEntry<ChunkLoadLambda>> = mutableListOf()
	private val unloadBuffer: MutableList<SingleBufferEntry<ChunkUnloadLambda>> = mutableListOf()

	fun addLoad(
		entry: ChunkLoadLambda,
		passthrough: BufferPassthrough = mutableListOf()
	): Boolean = this.loadBuffer.add(entry to passthrough)

	fun addUnload(
		entry: ChunkUnloadLambda,
		passthrough: BufferPassthrough = mutableListOf()
	): Boolean = this.unloadBuffer.add(entry to passthrough)

	fun handleLoad(
		event: ChunkEvent.Load
	) {
		this.loadBuffer.removeIf { it.first(event, it.second) }
	}

	fun handleUnload(
		event: ChunkEvent.Unload
	) {
		this.unloadBuffer.removeIf { it.first(event, it.second) }
	}
}