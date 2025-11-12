package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.tool_gun.gui.components.tool_gun_tabs.AbstractToolGunScreenTab
import org.bread_experts_group.breadmod.tool_gun.gui.screen.ToolGunScreen

class TabButton(
	message: Component,
	private val outerColor: Int,
	private val innerColor: Int,
	tab: AbstractToolGunScreenTab
) : Button(
	0,
	0,
	localClient.font.width(message) + 4,
	11,
	message,
	{ ToolGunScreen.activeTab = tab },
	{ Component.empty() }
) {
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		guiGraphics.borderedFill(
			this.x,
			this.y,
			this.x + this.width,
			this.y + this.height,
			this.outerColor,
			this.innerColor
		)
		this.renderString(guiGraphics, localClient.font, this.fgColor or (Mth.ceil(this.alpha * 255.0f) shl 24))
	}
}