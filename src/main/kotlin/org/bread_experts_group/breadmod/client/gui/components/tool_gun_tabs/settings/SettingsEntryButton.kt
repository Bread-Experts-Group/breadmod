package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsTab.Companion.currentSettingsEntry
import java.awt.Color

class SettingsEntryButton(
	message: Component,
	val description: Component,
	val entry: SettingsEntryEnums
) : Button(
	0,
	0,
	localClient.font.width(message) + 4,
	12,
	message,
	{ currentSettingsEntry = entry },
	{ Component.empty() }
) {
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.renderString(
			guiGraphics,
			localClient.font,
			if (!this.active) Color.GRAY.rgb else if (this.isHovered) Color(16755200).rgb else Color.WHITE.rgb
		)
	}
}