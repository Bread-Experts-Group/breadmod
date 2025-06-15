package org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.entries

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.settings.SettingsEntryEnum.MAIN
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen
import java.awt.Color

class MainEntry(
	screen: ToolGunScreen,
	stack: ItemStack
) : SettingsEntry(
	"main",
	MAIN,
	screen,
	stack,
	Component.literal("<")
) {
	override fun renderContainer(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
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
	}
}