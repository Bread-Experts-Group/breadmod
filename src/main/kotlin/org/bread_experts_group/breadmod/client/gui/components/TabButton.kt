package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth
import org.bread_experts_group.breadmod.client.render.borderedFill
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.gui.screens.ToolGunScreen
import org.bread_experts_group.breadmod.client.gui.components.tool_gun_tabs.ToolGunScreenTab
import java.awt.Color

class TabButton(
	message: Component,
	private val outerColor: Color,
	private val innerColor: Color,
	tab: ToolGunScreenTab
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
			RenderType.gui(),
			this.x,
			this.y,
			this.x + this.width,
			this.y + this.height,
			this.outerColor.rgb,
			this.innerColor.rgb
		)
		this.renderString(guiGraphics, localClient.font, this.fgColor or (Mth.ceil(this.alpha * 255.0f) shl 24))
	}
}