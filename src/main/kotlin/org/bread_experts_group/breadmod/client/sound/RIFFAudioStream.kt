package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.sounds.AudioStream
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.coder.format.riff.RIFFParser
import org.bread_experts_group.coder.format.riff.chunk.RIFFAudioFormatChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFContainerChunk
import org.lwjgl.BufferUtils
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

class RIFFAudioStream(private val riff: RIFFParser) : AudioStream {
	override fun close() {}

	private val descriptor: RIFFContainerChunk = this.riff.iterator().next() as RIFFContainerChunk
	private val audioFormatChunk: RIFFAudioFormatChunk =
		this.descriptor.firstNotNullOf { it as? RIFFAudioFormatChunk }
	private val wavData: RIFFChunk = this.descriptor.first { it.tag == "data" }
	val dataSize: Int = this.wavData.data.size
	var currentSlice: Int = 0
	var isPaused: Boolean = false

	fun togglePaused() {
		this.isPaused = !this.isPaused
	}

	init {
		LogManager.getLogger().info(this.audioFormatChunk)
		LogManager.getLogger().info(this.wavData)
		LogManager.getLogger().info(audioFormatChunk.sampleRate)
		LogManager.getLogger().info(audioFormatChunk.bitsPerSample)
	}

	override fun getFormat(): AudioFormat = AudioFormat(
		this.audioFormatChunk.sampleRate.toFloat(),
		this.audioFormatChunk.bitsPerSample,
		this.audioFormatChunk.numberOfChannels,
		true, false
	)

	override fun read(size: Int): ByteBuffer {
		val buffer: ByteBuffer = BufferUtils.createByteBuffer(size)
		var endSize = size + this.currentSlice

		if (endSize > this.dataSize) {
			val difference = endSize - this.dataSize
			endSize -= difference
		}
		val slicedArray = this.wavData.data.sliceArray(this.currentSlice until endSize)
		if (endSize != this.dataSize) this.currentSlice += size
		buffer.put(slicedArray)
		buffer.flip()
		return buffer
	}
}