package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.drawCenteredWordWrap
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryEnums.MAIN
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.ToolGunScreenTab
import org.bread_experts_group.breadmod.client.gui.components.TabButton
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries.MainEntry
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries.RendererEntry
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries.SettingsEntry
import java.awt.Color

class SettingsTab(screen: ToolGunScreen) : ToolGunScreenTab("settings", Color(0, 0, 180), screen) {
	companion object {
		var currentSettingsEntry: SettingsEntryEnums = MAIN
	}

	override fun getTabButton(): TabButton =
		TabButton(Component.literal("settings"), Color.BLUE, Color(0, 0, 230), this)

	override fun init() {
		Companion.currentSettingsEntry = MAIN
		this.addSettingsEntry(RendererEntry(this.screen))
		this.addSettingsEntry(MainEntry(this.screen))
		var entryPos = 30
		this.getWidgets().filterIsInstance<SettingsEntry>().forEach { entry ->
			this.addChild(
				"${entry.id}_button",
				entry.entryButton,
				this.x + 5,
				this.y + entryPos
			)
			entryPos += 12
		}
		(this.getChild("setting_entry_main_button") ?: return).setPosition(this.x + 5, this.y + 5)
	}

	override fun tick() {
		this.getWidgets().filterIsInstance<SettingsEntry>().forEach(SettingsEntry::tick)
	}

	private fun addSettingsEntry(entry: SettingsEntry) {
		entry.setPosition(this.x, this.y)
		entry.init()
		this.addChild(entry.id, entry)
	}

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.fill(
			this.x,
			this.y,
			this.x + 243,
			this.y + 185,
			Color(0, 0, 230, 255).rgb
		)
		this.getWidgets().filterIsInstance<SettingsEntryButton>()
			.asSequence()
			.filter { it.isHovered && it.visible && Companion.currentSettingsEntry == MAIN }
			.forEach {
				guiGraphics.drawCenteredWordWrap(
					localClient.font,
					it.description,
					this.screen.width / 2 + 133,
					this.y + 150,
					260,
					Color.WHITE.rgb
				)
			}
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
	}
}