package org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryButton
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.settings.SettingsEntryEnums.MAIN
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.render.localClient
import java.awt.Color

class MainEntry(screen: ToolGunScreen) : SettingsEntry(
	"main",
	MAIN,
	screen,
	SettingsEntryButton(Component.literal("<"), Component.empty(), MAIN)
) {
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.fill(
			this.x,
			this.y + 22,
			this.x + 243,
			this.y + 24,
			Color.WHITE.rgb
		)
		guiGraphics.fill(
			this.screen.width / 2,
			this.y + 22,
			this.screen.width / 2 + 2,
			this.y + 135,
			Color.WHITE.rgb
		)
		guiGraphics.fill(
			this.x,
			this.y + 135,
			this.x + 243,
			this.y + 137,
			Color.WHITE.rgb
		)
		guiGraphics.drawCenteredString(
			localClient.font,
			modTranslatable("tool_gun", "settings", "title"),
			this.screen.width / 2,
			this.y + 7,
			Color.WHITE.rgb
		)
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
	}
}