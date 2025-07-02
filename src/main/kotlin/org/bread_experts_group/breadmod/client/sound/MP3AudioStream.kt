package org.bread_experts_group.breadmod.client.sound

import net.minecraft.client.sounds.AudioStream
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.coder.format.mp3.MP3Parser
import org.bread_experts_group.coder.format.mp3.frame.MP3Frame
import org.bread_experts_group.coder.format.mp3.frame.header.MP3Header
import org.lwjgl.BufferUtils
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

class MP3AudioStream(private val from: MP3Parser) : AudioStream {
	val header: MP3Header = (this.from.first() as MP3Frame).header
	val mp3Data: ByteArray
	val dataSize: Int
	var currentSlice: Int = 0

	override fun close() {}

	init {
		val stream = ByteArrayOutputStream()
		this.from.forEach {
			if (it is MP3Frame) {
				LogManager.getLogger().info(it.data.size)
				stream.write(it.data)
			}
		}

		this.mp3Data = stream.toByteArray()
		this.dataSize = stream.size()
	}

	override fun getFormat(): AudioFormat = AudioFormat(
		this.header.sampleRate.raw.toFloat(),
		16,
		2,
		true, false
	)

	override fun read(size: Int): ByteBuffer {
		val buffer: ByteBuffer = BufferUtils.createByteBuffer(size)
		var endSize = size + this.currentSlice

		if (endSize > this.dataSize) {
			val difference = endSize - this.dataSize
			endSize -= difference
		}
		val slicedArray = this.mp3Data.sliceArray(this.currentSlice until endSize)
		if (endSize != this.dataSize) this.currentSlice += size
		buffer.put(slicedArray)
		buffer.flip()
		return buffer
	}
}