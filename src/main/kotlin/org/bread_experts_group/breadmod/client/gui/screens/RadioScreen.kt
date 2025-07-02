package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.playingSounds
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance

class RadioScreen(private val pos: BlockPos) : Screen(Component.empty()) {
	private var leftPos: Int = 0
	private var topPos: Int = 0

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 176, 100)
	}

	override fun init() {
		this.leftPos = (this.width - 176) / 2
		this.topPos = (this.height - 100) / 2

		// use url instead and get the headers for filenames
		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 5, 40, 20, "button") {
			Thread.ofVirtual().start {
				playingSounds[this.pos] = StereoSoundInstance(
					this::class.java.getResourceAsStream("/screw_the_nether.wav") ?: return@start,
					this.pos,
					50.0
				)
				(playingSounds[this.pos] ?: return@start).let(localClient.soundManager::play)
			}
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 30, 70, 20, "pause/play") {
			val instance = playingSounds[this.pos] ?: return@GenericButton
			val soundEngine = localClient.soundManager.soundEngine
			val channelHandle = soundEngine.instanceToChannel[instance] ?: return@GenericButton
			channelHandle.execute { channel ->
				LogManager.getLogger().info(channel.state)
				if (channel.state == 4114) {
					instance.riffAudioStream.togglePaused()
					channel.pause()
				} else if (channel.state == 4115) {
					instance.riffAudioStream.togglePaused()
					channel.unpause()
				}
//						channel.setRelative(true)
//						channel.linearAttenuation(10f)
//				AL10.alSourcef(channel.source, AL10.AL_GAIN, 1f)
			}
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 60, 70, 20, "rewind") {
		})
	}

	override fun isPauseScreen(): Boolean = false
}