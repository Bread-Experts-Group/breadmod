package org.bread_experts_group.breadmod.client.sound.stream

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.sounds.AudioStream
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.util.logDebugInfo
import org.bread_experts_group.coder.format.parse.id3.ID3Parser
import org.bread_experts_group.coder.format.parse.id3.frame.ID3CommentFrame
import org.bread_experts_group.coder.format.parse.id3.frame.ID3Header
import org.bread_experts_group.coder.format.parse.id3.frame.ID3PictureFrame2
import org.bread_experts_group.coder.format.parse.id3.frame.ID3PictureFrame3
import org.bread_experts_group.coder.format.parse.id3.frame.ID3PopularimeterFrame
import org.bread_experts_group.coder.format.parse.id3.frame.ID3TextFrame
import org.bread_experts_group.coder.format.parse.id3.frame.ID3URLLinkFrame
import org.lwjgl.BufferUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URI
import java.nio.ByteBuffer
import javax.imageio.ImageIO

abstract class BaseAudioStream(val uri: URI) : AudioStream {
	protected val logger: Logger = LogManager.getLogger()
	var title: Component = Component.empty() // MP3: TIT2 / TT2
	var artist: Component = Component.empty()// MP3: TPE1 / TP1
	var composer: Component = Component.empty() // MP3: TCOM / TCM
	var trackNumber: Component = Component.empty() // MP3: TRCK / TRK
	var albumTitle: Component = Component.empty() // MP3: TALB / TAL
	var set: Component = Component.empty() // MP3: TPOS / TPA
	var contentType: Component = Component.empty() // MP3: TCON / TCO
	var encodedBy: Component = Component.empty() // MP3: TENC

	//	var bandAccompaniment: Component = Component.empty() // MP3: TPE2 / TP2
	var copyright: Component = Component.empty() // MP3: TCOP

	//	var settingsForEncoding: Component = Component.empty() // MP3: TSSE
	var recordingDate: Component = Component.empty() // MP3: TDRC, WAV: ICRD

	//	var year: Component = Component.empty() // MP3: TYER, TYE
//	var commercialInfo: Component = Component.empty() // MP3: WCOM / WCM
//	var copyrightInfo: Component = Component.empty() // MP3: WCOP / WCP
//	var audioFileWebpage: Component = Component.empty() // MP3: WOAF / WAF
//	var artistWebpage: Component = Component.empty() // MP3: WOAR / WAR
//	var audioWebpage: Component = Component.empty() // MP3: WOAS / WAS
//	var radioStationWebpage: Component = Component.empty() // MP3: WORS
//	var paymentLink: Component = Component.empty() // MP3: WPAY
//	var publisherHomepage: Component = Component.empty() // MP3: WPUB
//	var popularimeter: Component = Component.empty() // MP3: POPM / POP
	var comments: Component = Component.empty() // MP3: COMM / COM

	//	var unsyncedLyrics: Component = Component.empty() // MP3: USLT / ULT
	var genre: Component = Component.empty() // WAV: IGNR
	var extra: MutableList<Component> = mutableListOf<Component>()

	fun decodeMetadata(id3: ID3Parser) {
		val id3Header = (id3.firstOrNull() ?: return).resultSafe as ID3Header
		val version = "(v${id3Header.major}.${id3Header.minor})"
		for (f in id3) when (val f = f.resultSafe) {
			is ID3TextFrame -> when (f.tag) {
				"TIT2", "TT2" -> this.title = this.setComp(f.text)
				"TPE1", "TP1" -> this.artist = this.setComp(f.text)
				"TCOM", "TCM" -> this.composer = this.setComp(f.text)
				"TRCK", "TRK" -> this.trackNumber = this.setComp(f.text)
				"TCOP", "TCR" -> this.copyright = this.setComp(f.text)
				"TALB", "TAL" -> this.albumTitle = this.setComp(f.text)
				"TCON", "TCO" -> this.contentType = this.setComp(f.text)
				"TDRC" -> this.recordingDate = this.setComp(f.text)
				else -> {
					if (f.tag != "TXXX" && f.tag != "TXX")
						this.logger.warn("no support for ${f.tag} $version of type ID3TextFrame")
					this.extra.add(this.setComp(f.text))
				}
			}
			is ID3URLLinkFrame -> this.logger.warn("[uri] no support for $f $version")
			is ID3PopularimeterFrame -> this.logger.warn("ID3PopularimeterFrame (todo $version, $f)")
			is ID3CommentFrame -> this.comments = Component.literal(f.text)
			is ID3PictureFrame2 -> this.setImageData(f.data, f.imageType)
			is ID3PictureFrame3 -> this.setImageData(f.data, f.mimeType)
			else -> this.logger.warn("Unsupported frame type: $f")
		}
	}

	var image: ImageData = ImageData.EMPTY // MP3: APIC / PIC
	var isPaused: Boolean = false
	abstract val audioData: ByteArray
	var currentSlice: Int = 0
	val dataSize: Int
		get() = this.audioData.size

	fun togglePaused() {
		this.isPaused = !this.isPaused
	}

	fun setComp(value: Array<String>): Component = Component.literal(value.joinToString(", "))

	fun setImageData(data: ByteArray, imageType: String) {
		val stream = ByteArrayInputStream(data)
		val nativeImage: NativeImage = if (imageType == "image/jpeg" || imageType == "JPG") {
			logDebugInfo("converting JPG to PNG...")
			val output = ByteArrayOutputStream()
			ImageIO.write(ImageIO.read(stream), "png", output)
			NativeImage.read(ByteArrayInputStream(output.toByteArray()))
		} else NativeImage.read(ByteArrayInputStream(data))
		val texture = DynamicTexture(nativeImage)
		val location = modLocation("image", this.uri.toASCIIString().replace(Regex("[^A-Za-z0-9]"), "_"))
		this.image = ImageData(nativeImage.width, nativeImage.height, texture, location)
		this.image.registerTexture()
	}

	override fun close() {}

	override fun read(size: Int): ByteBuffer {
		val buffer: ByteBuffer = BufferUtils.createByteBuffer(size)
		var endSize = size + this.currentSlice
		val dataSize = this.audioData.size

		if (endSize > dataSize) {
			val difference = endSize - dataSize
			endSize -= difference
		}
		val slicedArray = this.audioData.sliceArray(this.currentSlice until endSize)
		if (endSize != dataSize) this.currentSlice += size
		buffer.put(slicedArray)
		buffer.flip()
		return buffer
	}
}