package org.bread_experts_group.breadmod.client.render.buffer.chunk

import net.neoforged.neoforge.event.level.ChunkEvent.Load
import net.neoforged.neoforge.event.level.ChunkEvent.Unload
import org.bread_experts_group.breadmod.client.render.buffer.render.BufferLambda
import org.bread_experts_group.breadmod.client.render.buffer.render.BufferPassthrough
import org.bread_experts_group.breadmod.client.render.buffer.render.SingleBufferEntry

private typealias ChunkLoadLambda = BufferLambda<Load>
private typealias ChunkUnloadLambda = BufferLambda<Unload>

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
		event: Load
	) {
		this.loadBuffer.removeIf { it.first(event, it.second) }
	}

	fun handleUnload(
		event: Unload
	) {
		this.unloadBuffer.removeIf { it.first(event, it.second) }
	}
}