package org.bread_experts_group.breadmod.client.sound.stream

import org.bread_experts_group.coder.format.mp3.MP3Parser
import org.bread_experts_group.coder.format.mp3.frame.MP3Frame
import org.bread_experts_group.coder.format.mp3.frame.MP3ID3Frame
import org.bread_experts_group.coder.format.mp3.frame.header.MP3Header
import org.lwjgl.BufferUtils
import java.io.ByteArrayOutputStream
import java.net.URI
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

class MP3AudioStream(uri: URI) : BaseAudioStream(uri) {
	private val headers: Array<MP3Header>
	override var audioData: ByteArray

	override fun getFormat(): AudioFormat = AudioFormat(
		this.headers[0].sampleRate.toFloat(),
		16,
		2,
		true, false
	)

	override fun read(size: Int): ByteBuffer = BufferUtils.createByteBuffer(size)

	init {
		val headers = mutableListOf<MP3Header>()
		val stream = ByteArrayOutputStream()
		for (f in MP3Parser(this.uri.toURL().openStream())) when (f) {
			is MP3ID3Frame -> this.decodeMetadata(f.id3)
			is MP3Frame -> {
				headers.add(f.header)
				stream.write(f.data)
			}
		}
		this.audioData = stream.toByteArray()
		this.headers = headers.toTypedArray()
	}
}