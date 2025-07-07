package org.bread_experts_group.breadmod.client.sound.stream

import com.mojang.blaze3d.platform.NativeImage
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.client.sounds.AudioStream
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.lwjgl.BufferUtils
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.nio.ByteBuffer
import javax.imageio.ImageIO

abstract class BaseAudioStream(val url: URL) : AudioStream {
	var title: Component = Component.empty() // MP3: TIT2 / TT2
	var artist: Component = Component.empty()// MP3: TPE1 / TP1
	var composer: Component = Component.empty() // MP3: TCOM / TCM
	var trackNumber: Component = Component.empty() // MP3: TRCK / TRK
	var albumTitle: Component = Component.empty() // MP3: TALB / TAL
	var set: Component = Component.empty() // MP3: TPOS / TPA
	var contentType: Component = Component.empty() // MP3: TCON / TCO
	var encodedBy: Component = Component.empty() // MP3: TENC
	var bandAccompaniment: Component = Component.empty() // MP3: TPE2 / TP2
	var copyright: Component = Component.empty() // MP3: TCOP
	var settingsForEncoding: Component = Component.empty() // MP3: TSSE
	var year: Component = Component.empty() // MP3: TYER, TYE
	var commercialInfo: Component = Component.empty() // MP3: WCOM / WCM
	var copyrightInfo: Component = Component.empty() // MP3: WCOP / WCP
	var audioFileWebpage: Component = Component.empty() // MP3: WOAF / WAF
	var artistWebpage: Component = Component.empty() // MP3: WOAR / WAR
	var audioWebpage: Component = Component.empty() // MP3: WOAS / WAS
	var radioStationWebpage: Component = Component.empty() // MP3: WORS
	var paymentLink: Component = Component.empty() // MP3: WPAY
	var publisherHomepage: Component = Component.empty() // MP3: WPUB
	var popularimeter: String = "" // MP3: POPM / POP
	var comments: String = "" // MP3: COMM / COM
	var unsyncedLyrics: String = "" // MP3: USLT / ULT
	var image: ImageData = ImageData.EMPTY // MP3: APIC / PIC
	var isPaused: Boolean = false
	abstract var audioData: ByteArray
	var currentSlice: Int = 0
	val dataSize: Int
		get() = this.audioData.size
	var initialized: Boolean = false

	fun togglePaused() {
		this.isPaused = !this.isPaused
	}

	fun setComp(value: String): Component = Component.literal(value)

	fun setImageData(data: ByteArray, imageType: String) {
		val stream = ByteArrayInputStream(data)
		val nativeImage: NativeImage = if (imageType == "image/jpeg" || imageType == "JPG") {
			LogManager.getLogger().info("converting JPG to PNG...")
			val output = ByteArrayOutputStream()
			ImageIO.write(ImageIO.read(stream), "png", output)
			NativeImage.read(ByteArrayInputStream(output.toByteArray()))
		} else NativeImage.read(ByteArrayInputStream(data))
		val texture = DynamicTexture(nativeImage)
		val location = modLocation("image", this.url.file.substringBefore('.'))
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