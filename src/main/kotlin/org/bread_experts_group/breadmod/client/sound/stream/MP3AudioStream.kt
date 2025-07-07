package org.bread_experts_group.breadmod.client.sound.stream

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.coder.format.id3.frame.ID3CommentFrame
import org.bread_experts_group.coder.format.id3.frame.ID3Header
import org.bread_experts_group.coder.format.id3.frame.ID3PictureFrame2
import org.bread_experts_group.coder.format.id3.frame.ID3PictureFrame3
import org.bread_experts_group.coder.format.id3.frame.ID3PopularimeterFrame
import org.bread_experts_group.coder.format.id3.frame.ID3TextFrame
import org.bread_experts_group.coder.format.id3.frame.ID3URLLinkFrame
import org.bread_experts_group.coder.format.mp3.MP3Parser
import org.bread_experts_group.coder.format.mp3.frame.MP3Frame
import org.bread_experts_group.coder.format.mp3.frame.MP3ID3Frame
import org.bread_experts_group.coder.format.mp3.frame.header.MP3Header
import org.lwjgl.BufferUtils
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.ByteBuffer
import javax.sound.sampled.AudioFormat

class MP3AudioStream(url: URL) : BaseAudioStream(url) {
	private val parser: MP3Parser = MP3Parser(this.url.openStream())
	private val header: MP3Header
	override var audioData: ByteArray
	private val logger: Logger = LogManager.getLogger("[MP3AudioStream]")

	override fun getFormat(): AudioFormat = AudioFormat(
		this.header.sampleRate.toFloat(),
		16,
		2,
		true, false
	)

	override fun read(size: Int): ByteBuffer = BufferUtils.createByteBuffer(size)

	init {
		val stream = ByteArrayOutputStream()
		val id3Parser = (this.parser.first() as MP3ID3Frame).id3
		this.header = (this.parser.first() as MP3Frame).header
		val iD3Header = id3Parser.first() as ID3Header
		val version = "(v${iD3Header.major}.${iD3Header.minor})"

		for (i in id3Parser) when (i) {
			is ID3TextFrame          -> when (i.tag) {
				"TIT2", "TT2" -> this.title = this.setComp(i.text.first())
				"TPE1", "TP1" -> this.artist = this.setComp(i.text.first())
				"TCOM", "TCM" -> this.composer = this.setComp(i.text.first())
				"TRCK", "TRK" -> this.trackNumber = this.setComp(i.text.first())
				"TCOP", "TCR" -> this.copyright = this.setComp(i.text.first())
				else          -> this.logger.warn("no support for ${i.tag} $version of type ID3TextFrame")
			}
			is ID3URLLinkFrame       -> when {
				else -> this.logger.warn("no support for ${i.tag} $version of type ID3URLLinkFrame")
			}
			is ID3PopularimeterFrame -> this.logger.warn("ID3PopularimeterFrame (todo $version)")
			is ID3CommentFrame       -> when (i.tag) {
				"COMM", "COM" -> this.comments = i.text
			}
			is ID3PictureFrame2      -> this.setImageData(i.data, i.imageType)
			is ID3PictureFrame3      -> this.setImageData(i.data, i.mimeType)
			else                     -> this.logger.warn("Unsupported frame type: $i")
		}

		this.parser.forEach {
			if (it is MP3Frame) {
				stream.write(it.data)
			}
		}

		this.audioData = stream.toByteArray()
		this.initialized = true
	}
}