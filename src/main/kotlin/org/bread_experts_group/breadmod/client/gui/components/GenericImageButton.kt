package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.texture.GuiElement

class GenericImageButton(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	private val image: GuiElement,
	tooltipMessage: Component = Component.empty(),
	onClick: (Button, Int) -> Unit
) : GenericButton(x, y, width, height, Component.empty(), tooltipMessage, onClick) {
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.renderTooltipMessage(guiGraphics, mouseX, mouseY)
		this.image.blitScaled(guiGraphics, this.x, this.y, this.width, this.height)
	}
}