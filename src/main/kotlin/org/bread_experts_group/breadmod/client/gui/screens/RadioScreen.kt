package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.playingSounds
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import java.net.URI
import java.net.URL

class RadioScreen(private val pos: BlockPos) : Screen(Component.empty()) {
	private var leftPos: Int = 0
	private var topPos: Int = 0
	private val logger: Logger = LogManager.getLogger("RadioScreen")

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 176, 150)

		playingSounds[this.pos]?.stream?.image?.let {
			guiGraphics.pose().pushPose()
			guiGraphics.pose().scaleFlat(0.5f)
			guiGraphics.blit(it.location, this.leftPos, this.topPos, 0f, 0f, 128, 128, 128, 128)
			guiGraphics.pose().popPose()
		}
	}

	private var url: URL? = null

	init {
		val playingSound = playingSounds[this.pos]
		if (playingSound != null) {
			this.url = playingSound.url
		}
	}

	private fun trySetURL(value: String) {
		val extension = value.substringAfter('.').lowercase()
		val flag = when (extension) {
			"mp3" -> true
			"wav" -> true
			else  -> false
		}

		if (flag) try {
			val uri = URI(value)
			this.url = uri.toURL()
		} catch (e: Exception) {
			this.logger.error(e)
		}

		if (this.url == null) this.logger.error("Invalid url? [$value]")
		else this.logger.info("Successfully set url to: ${this.url?.path}")
	}

	override fun init() {
		this.leftPos = (this.width - 176) / 2
		this.topPos = (this.height - 150) / 2

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 5, 40, 20, "button") {
			Thread.ofVirtual().start {
				playingSounds[this.pos] = StereoSoundInstance(this.url ?: return@start, this.pos)
				val instance = playingSounds[this.pos] ?: return@start
				if (instance.stream.initialized) localClient.soundManager.play(instance)
			}
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 45, this.topPos + 5, 40, 20, "test") {
			Thread.ofVirtual().start {
				this@RadioScreen.trySetURL("file:///home/logan/beginning.mp3")
				playingSounds[this.pos] = StereoSoundInstance(this.url ?: return@start, this.pos)
				val instance = playingSounds[this.pos] ?: return@start
				if (instance.stream.initialized) localClient.soundManager.play(instance)
			}
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 30, 70, 20, "pause/play") {
			val instance = playingSounds[this.pos] ?: return@GenericButton
			instance.togglePause()
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 60, 70, 20, "rewind") {
		})

		this.addRenderableWidget(
			object : EditBox(this.font, this.leftPos + 5, this.topPos + 90, 165, 20, Component.empty()) {
				init {
					this.value = this@RadioScreen.url?.path ?: ""
					this.setMaxLength(100)
				}

				override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
					if (keyCode == 257) this@RadioScreen.trySetURL(this.value)
					return super.keyPressed(keyCode, scanCode, modifiers)
				}
			})
	}

	override fun isPauseScreen(): Boolean = false
}