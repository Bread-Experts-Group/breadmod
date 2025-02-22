package org.bread_experts_group.breadmod.client.tool_gun

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.ModTextureLocations

class ToolGunScreen(title: Component) : Screen(title) {
	companion object {
		var activeTab: ToolGunScreenTab? = null
	}

	private var leftPos: Int = (this.width - 256) / 2
	private var topPos: Int = (this.height - 256) / 2

	override fun isPauseScreen(): Boolean = false
	override fun renderBackground(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.getTabs().forEach { it.active = Companion.activeTab == it; it.visible = Companion.activeTab == it }
		super.renderBackground(guiGraphics, mouseX, mouseY, partialTick)
		ModTextureLocations.FRAME.blitTexture(guiGraphics, this.leftPos, this.topPos)

		Companion.activeTab?.let {
			guiGraphics.fill(
				this.leftPos + 7,
				this.topPos + 27,
				this.leftPos + 250,
				this.topPos + 38,
				it.tabBarColor.rgb
			)
		}
	}

	fun getTabs(): List<ToolGunScreenTab> = this.children().filterIsInstance<ToolGunScreenTab>()

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
		if (keyCode == InputConstants.KEY_E) {
			this.onClose()
			true
		} else super.keyPressed(keyCode, scanCode, modifiers)

	override fun init() {
		this.leftPos = (this.width - 256) / 2
		this.topPos = (this.height - 256) / 2

		this.addRenderableWidget(ModeSelectTab(this.leftPos + 7, this.topPos + 38).also { Companion.activeTab = it })
		this.addRenderableWidget(SettingsTab(this.leftPos + 7, this.topPos + 38))
		var tabPosition = this.leftPos + 7
		this.getTabs().forEach {
			this.addRenderableWidget(it.getTabButton().also { button ->
				button.setPosition(tabPosition, this.topPos + 27)
				tabPosition += button.width
			})
		}
	}
}