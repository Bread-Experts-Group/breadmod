package org.bread_experts_group.breadmod.tool_gun.gui.screen

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.level.Level
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.GenericButton
import org.bread_experts_group.breadmod.client.gui.screens.HoldScreen
import org.bread_experts_group.breadmod.client.render.fillPositioned
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.data_holders.common.ToolGunData
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_tabs.BlockTab
import org.bread_experts_group.breadmod.tool_gun.gui.components.creator_tabs.EntityTab
import org.bread_experts_group.breadmod.tool_gun.sound.KSPSoundInstance
import org.bread_experts_group.breadmod.util.Color

// todo abstract tickable sound instance that fades in the KSP music when you open it, and fades out when you exit
//  fade back in if the screen is re-opened mid fade, and vice versa
class CreatorScreen(
	private val level: Level,
	val data: ToolGunData
) : HoldScreen(Component.empty(), InputConstants.KEY_F) {
	companion object {
		val bgSound: KSPSoundInstance = KSPSoundInstance()
	}

	var lastTick: Int = 0
	var leftPos: Int = 0
	var topPos: Int = 0

	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
//		guiGraphics.borderedFillPositioned(this.leftPos, this.topPos, 256, 256, Color.GRAY, Color.BLACK)
		RenderSystem.enableBlend()
		guiGraphics.fillPositioned(this.leftPos, this.topPos + 12, 256, 244, Color.color(a = 100))
		RenderSystem.disableBlend()
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
	}

	override val shouldClose: Boolean
		get() = false

	fun getGuiTicks(): Int = localClient.gui.guiTicks

	override fun init() {
		this.leftPos = (this.width - 256) / 2
		this.topPos = (this.height - 256) / 2
		this.addRenderableWidget(BlockTab(this, this.level))
		this.addRenderableWidget(EntityTab(this, this.level))
		this.addRenderableWidget(GenericButton(this.leftPos, this.topPos, 44, 12, "BLOCK") {
			(this.children()[0] as ContainerWidget<*>).enable()
			(this.children()[1] as ContainerWidget<*>).disable()
		})
		this.addRenderableWidget(GenericButton(this.leftPos + 44, this.topPos, 44, 12, "ENTITY") {
			(this.children()[0] as ContainerWidget<*>).disable()
			(this.children()[1] as ContainerWidget<*>).enable()
		})
		Companion.bgSound.shouldPlay = true
		if (!localClient.soundManager.isActive(Companion.bgSound)) localClient.soundManager.play(Companion.bgSound)
	}

	override fun onClose() {
		Companion.bgSound.shouldPlay = false
		super.onClose()
	}

	override fun isPauseScreen(): Boolean = false
}