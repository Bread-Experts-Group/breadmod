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

	private val descriptor: RIFFContainerChunk = this.riff.readParsed() as RIFFContainerChunk
	private val audioFormatChunk: RIFFAudioFormatChunk = this.descriptor.firstNotNullOf { it as? RIFFAudioFormatChunk }
	private val wavData: RIFFChunk = this.descriptor.first { it.tag == "data" }

	init {
		LogManager.getLogger().info(this.audioFormatChunk)
		LogManager.getLogger().info(this.wavData)
	}

	override fun getFormat(): AudioFormat = AudioFormat(
		this.audioFormatChunk.sampleRate.toFloat(),
		this.audioFormatChunk.bitsPerSample,
		this.audioFormatChunk.numberOfChannels,
		true, false
	)

	private var currentSlice: Int = 0
	override fun read(size: Int): ByteBuffer {
		val buffer: ByteBuffer = BufferUtils.createByteBuffer(size)
//		val arraySize = this.wavData.data.size
//		if (this.slice + size > arraySize)
		val slicedArray = this.wavData.data.sliceArray(this.currentSlice until size + this.currentSlice)
		this.currentSlice += size
		buffer.put(slicedArray)
		buffer.flip()
		return buffer
	}
}