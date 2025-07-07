package org.bread_experts_group.breadmod.client.sound.stream

import net.minecraft.network.chat.Component
import org.bread_experts_group.coder.format.riff.RIFFParser
import org.bread_experts_group.coder.format.riff.chunk.RIFFAudioFormatChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFContainerChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFID3Chunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFTextChunk
import java.net.URI
import javax.sound.sampled.AudioFormat

class RIFFAudioStream(uri: URI) : BaseAudioStream(uri) {
	private val audioFormatChunk: RIFFAudioFormatChunk
	override val audioData: ByteArray

	init {
		lateinit var preppedFormat: RIFFAudioFormatChunk
		lateinit var preppedData: ByteArray
		val container = RIFFParser(this.uri.toURL().openStream()).first() as RIFFContainerChunk
		if (container.localIdentifier != "WAVE") throw IllegalArgumentException("Not a .wav file!")
		for (c in container) when (c) {
			is RIFFAudioFormatChunk if c.tag == "fmt " -> preppedFormat = c
			is RIFFContainerChunk if c.tag == "LIST" && c.localIdentifier == "INFO" -> for (i in c) when (i) {
				is RIFFTextChunk if i.tag == "ICMT" -> this.comments = Component.literal(i.text)
				is RIFFTextChunk if i.tag == "ITRK" -> this.trackNumber = Component.literal(i.text)
				is RIFFTextChunk if i.tag == "ISFT" -> this.encodedBy = Component.literal(i.text)
				is RIFFTextChunk if i.tag == "ICRD" -> this.recordingDate = Component.literal(i.text)
				is RIFFTextChunk if i.tag == "IGNR" -> this.genre = Component.literal(i.text)
				else -> this.logger.info("Notice: unrecognized INFO text chunk [$i]!")
			}
			is RIFFID3Chunk -> this.decodeMetadata(c.id3)
			else ->
				if (c.tag == "data") preppedData = c.data
				else this.logger.info("Notice: unrecognized chunk [$c]!")
		}
		this.audioFormatChunk = preppedFormat
		this.audioData = preppedData
	}

	override fun getFormat(): AudioFormat = AudioFormat(
		this.audioFormatChunk.sampleRate.toFloat(),
		this.audioFormatChunk.bitsPerSample,
		this.audioFormatChunk.numberOfChannels,
		true, false
	)
}