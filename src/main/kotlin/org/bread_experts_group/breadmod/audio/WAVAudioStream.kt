package org.bread_experts_group.breadmod.audio

import it.unimi.dsi.fastutil.floats.FloatConsumer
import net.minecraft.client.sounds.FloatSampleSource
import org.bread_experts_group.coder.format.riff.RIFFInputStream
import javax.sound.sampled.AudioFormat
import kotlin.random.Random

class WAVAudioStream(private val from: RIFFInputStream) : FloatSampleSource {
	override fun readChunk(p0: FloatConsumer): Boolean {
		repeat(44100) {
			p0.accept(Random.nextFloat())
		}
		return true
	}

	override fun getFormat(): AudioFormat = AudioFormat(
		44100F,
		0,
		2,
		true, false
	)

	override fun close(): Unit = this.from.close()
}