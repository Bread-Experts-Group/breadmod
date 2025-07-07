package org.bread_experts_group.breadmod.client.sound.stream

import org.apache.logging.log4j.LogManager
import org.bread_experts_group.coder.format.riff.RIFFParser
import org.bread_experts_group.coder.format.riff.chunk.RIFFAudioFormatChunk
import org.bread_experts_group.coder.format.riff.chunk.RIFFContainerChunk
import java.net.URL
import javax.sound.sampled.AudioFormat


class RIFFAudioStream(url: URL) : BaseAudioStream(url) {
//	private val parser: RIFFParser = RIFFParser(this.url.openStream())
//	private val waveChunk: RIFFContainerChunk? =
//		this.parser.filterIsInstance<RIFFContainerChunk>().find { it.localIdentifier == "WAVE" }
//	private var audioFormatChunk: RIFFAudioFormatChunk = this.waveChunk!!.firstNotNullOf { it as? RIFFAudioFormatChunk }
//	override var audioData: ByteArray = this.waveChunk!!.first { it.tag == "data" }.data

	private lateinit var audioFormatChunk: RIFFAudioFormatChunk
	override lateinit var audioData: ByteArray

	init {
		RIFFParser(this.url.openStream()).forEach {
			LogManager.getLogger().info((it as? RIFFContainerChunk)?.localIdentifier)
			(it as? RIFFContainerChunk)?.forEach { LogManager.getLogger().info(it.tag) }
		}
		for (i in RIFFParser(this.url.openStream())) {
			if (i !is RIFFContainerChunk) continue
			when (i.localIdentifier) {
				"fmt"  -> this.audioFormatChunk = i.firstNotNullOf { it as? RIFFAudioFormatChunk }
				"data" -> {
					LogManager.getLogger().info(i.data.size)
					this.audioData = i.data
				}
				"id3"  -> LogManager.getLogger().info(i.localIdentifier)
			}
		}

		this.initialized = true
	}

	override fun getFormat(): AudioFormat = AudioFormat(
		this.audioFormatChunk.sampleRate.toFloat(),
		this.audioFormatChunk.bitsPerSample,
		this.audioFormatChunk.numberOfChannels,
		true, false
	)
}