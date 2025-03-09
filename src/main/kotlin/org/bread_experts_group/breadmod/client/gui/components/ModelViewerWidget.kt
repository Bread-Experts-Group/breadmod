package org.bread_experts_group.breadmod.client.gui.components

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.localClient

/*
Holding left click rotates the model
Holding right click moves the model
Scrolling zooms the model

Buttons for fine tuning the movement and zoom

Only start dragging when the mouse is in the preview window or the drag flag is set to true

Only allow zooming when mouse is within the preview window

Make this whole thing a widget and invoke a lambda containing the model rendering code
 */

class ModelViewerWidget(
	x: Int,
	y: Int,
	val model: (PoseStack, MultiBufferSource) -> Unit // todo figure out what to pass for rendering a model
) : AbstractWidget(x, y, 200, 200, Component.empty()) {
	override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		this.model.invoke(guiGraphics.pose(), localClient.renderBuffers().bufferSource())
	}

	override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
	}
}