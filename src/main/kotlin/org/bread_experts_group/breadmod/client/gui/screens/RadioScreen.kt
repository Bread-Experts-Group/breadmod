package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.components.GenericEditBox
import org.bread_experts_group.breadmod.client.render.scaleFlat
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.client.sound.StereoSoundInstance
import org.bread_experts_group.breadmod.registry.Registry.playingSounds
import java.net.URI

class RadioScreen(private val pos: BlockPos) : Screen(Component.empty()) {
	private var leftPos: Int = 0
	private var topPos: Int = 0
	private val logger: Logger = LogManager.getLogger("RadioScreen")

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 176, 150)
		val pos = this.pos.center
		(playingSounds[pos] as? StereoSoundInstance)?.stream?.image?.let {
			guiGraphics.pose().pushPose()
			guiGraphics.pose().scaleFlat(0.5f)
			guiGraphics.blit(it.location, this.leftPos, this.topPos, 0f, 0f, 128, 128, 128, 128)
			guiGraphics.pose().popPose()
		}
	}

	private var uri: URI? = null

	init {
		val playingSound = playingSounds[this.pos.center] as? StereoSoundInstance
		if (playingSound != null) this.uri = playingSound.uri
	}

	private fun trySetURL(value: String) {
		val extension = value.substringAfter('.').lowercase()
		val uri = try {
			if (extension == "mp3" || extension == "wav") URI(value)
			else null
		} catch (e: Exception) {
			e.printStackTrace()
			null
		}

		if (uri == null) this.logger.error("Invalid url? [$value]")
		else {
			this.uri = uri
			this.logger.info("Successfully set url to: ${uri.path}")
		}
	}

	override fun init() {
		this.leftPos = (this.width - 176) / 2
		this.topPos = (this.height - 150) / 2

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 5, 40, 20, "button") { _, _ ->
			Thread.ofVirtual().start {
				val pos = this.pos.center
				playingSounds[pos] = StereoSoundInstance(this.uri ?: return@start, pos)
				(playingSounds[pos] ?: return@start).play()
			}
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 45, this.topPos + 5, 40, 20, "test") { _, _ ->
			Thread.ofVirtual().start {
				val pos = this.pos.center
				this@RadioScreen.trySetURL("file:///home/logan/beginning.mp3")
				playingSounds[pos] = StereoSoundInstance(this.uri ?: return@start, pos)
				(playingSounds[pos] ?: return@start).play()
			}
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 30, 70, 20, "pause/play") { _, _ ->
			val pos = this.pos.center
			val instance = playingSounds[pos] ?: return@GenericButton
			instance.togglePause()
		})

		this.addRenderableWidget(GenericButton(this.leftPos + 5, this.topPos + 60, 70, 20, "rewind") { _, _ ->
		})

		this.addRenderableWidget(
			GenericEditBox(this.leftPos + 5, this.topPos + 90, 165, 20, Component.empty(), { editBox ->
				editBox.value = this@RadioScreen.uri?.path ?: ""
				editBox.setMaxLength(100)
			}) { editBox, keyCode ->
				if (keyCode == 257) this@RadioScreen.trySetURL(editBox.value)
			})
	}

	override fun isPauseScreen(): Boolean = false
}