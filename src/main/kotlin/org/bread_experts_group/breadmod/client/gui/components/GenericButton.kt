package org.bread_experts_group.breadmod.client.gui.components

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.contents.PlainTextContents
import org.bread_experts_group.breadmod.client.render.localClient

class GenericButton(
	x: Int,
	y: Int,
	width: Int,
	height: Int,
	message: Component,
	private val tooltipMessage: Component = Component.empty(),
	onPress: OnPress
) :
	Button(x, y, width, height, message, onPress, { Component.empty() }) {
	constructor(
		x: Int, y:
		Int, width:
		Int, height: Int,
		message: String,
		tooltipMessage: Component = Component.empty(),
		onPress: OnPress
	) : this(x, y, width, height, Component.literal(message), tooltipMessage, onPress)

	override fun getTooltip(): Tooltip = Tooltip.create(this.tooltipMessage)

	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		if (this.isMouseOver(
				mouseX.toDouble(),
				mouseY.toDouble()
			) && this.tooltipMessage.contents != PlainTextContents.EMPTY
		) guiGraphics.renderTooltip(localClient.font, this.tooltipMessage, mouseX, mouseY)
		super.renderWidget(guiGraphics, mouseX, mouseY, partialTick)
	}
}