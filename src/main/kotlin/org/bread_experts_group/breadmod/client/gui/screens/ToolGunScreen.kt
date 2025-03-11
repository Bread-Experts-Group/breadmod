package org.bread_experts_group.breadmod.client.gui.screens

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.client.gui.components.ContainerWidget
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.ModeSelectTab
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.ToolGunScreenTab
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsTab

class ToolGunScreen(title: Component) : Screen(title) {
	companion object {
		var activeTab: ToolGunScreenTab? = null
	}

	/**
	 * Starts at the top left of the gui and moves left to right
	 */
	var leftPos: Int = (this.width - 256) / 2

	/**
	 * Starts at the top left of the gui and moves up to down
	 */
	var topPos: Int = (this.height - 256) / 2

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

	private fun getTabs(): List<ToolGunScreenTab> = this.children().filterIsInstance<ToolGunScreenTab>()
	private fun getTabByID(id: String): ToolGunScreenTab = this.getTabs().first { it.id == id }

	override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean =
		if (keyCode == InputConstants.KEY_E) {
			this.onClose()
			true
		} else super.keyPressed(keyCode, scanCode, modifiers)

	override fun init() {
		this.leftPos = (this.width - 256) / 2
		this.topPos = (this.height - 256) / 2

		this.addTab(ModeSelectTab(this), true)
		this.addTab(SettingsTab(this), false)
		var tabPosition = this.leftPos + 7
		this.getTabs().forEach {
			this.addRenderableWidget(it.getTabButton().also { button ->
				button.setPosition(tabPosition, this.topPos + 27)
				tabPosition += button.width
			})
		}
	}

	override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
		this.children().any { child ->
			if (child.mouseClicked(mouseX, mouseY, button)) {
				if (child !is ContainerWidget<*>) this.focused = child
				if (button == 0) this.isDragging = true
				return true
			} else false
		}

		return false
	}

	override fun tick() {
		this.children().filterIsInstance<ContainerWidget<*>>().forEach(ContainerWidget<*>::tick)
	}

	override fun rebuildWidgets() {
		val tab = this.getTabByID("mode_select") as? ModeSelectTab ?: return super.rebuildWidgets()
		Companion.activeTab = tab
		super.rebuildWidgets()
	}

	private fun addTab(tab: ToolGunScreenTab, initialTab: Boolean) {
		if (initialTab) Companion.activeTab = tab
		tab.setPosition(this.leftPos + 7, this.topPos + 38)
		tab.init()
		this.addRenderableWidget(tab)
	}
}