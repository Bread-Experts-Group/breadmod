package org.bread_experts_group.breadmod.client.sound.stream

import org.bread_experts_group.coder.format.parse.id3.ID3Parser
import org.bread_experts_group.coder.format.parse.mp3.MP3Parser
import org.bread_experts_group.coder.format.parse.mp3.frame.MP3Frame
import org.bread_experts_group.coder.format.parse.mp3.frame.MP3ID3Frame
import org.bread_experts_group.coder.format.parse.mp3.frame.header.ChannelMode
import org.bread_experts_group.coder.format.parse.mp3.frame.header.MP3Header
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
		this.numberOfChannels(),
		true, false
	)

	private fun numberOfChannels(): Int = when (this.headers[0].channelMode) {
		ChannelMode.DUAL_CHANNEL, ChannelMode.STEREO, ChannelMode.JOINT_STEREO -> 2
		ChannelMode.SINGLE_CHANNEL -> 1
	}

	override fun read(size: Int): ByteBuffer = BufferUtils.createByteBuffer(size)

	init {
		val headers = mutableListOf<MP3Header>()
		val stream = ByteArrayOutputStream()
		for (f in MP3Parser().setInput(this.uri.toURL().openStream())) when (val f = f.resultSafe) {
			is MP3ID3Frame -> this.decodeMetadata(f.id3 as ID3Parser)
			is MP3Frame -> {
				headers.add(f.header)
				stream.write(f.data)
			}
		}
		this.audioData = stream.toByteArray()
		this.headers = headers.toTypedArray()
	}
}