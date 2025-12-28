package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.util.Color

class DoughMachineScreen(
	menu: DoughMachineMenu,
	title: Component
) : BreadModScreen<DoughMachineMenu>(menu, title) {
	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 176, 166)
		ModGuiElements.DOUGH_MACHINE_ARROW.blit(guiGraphics, this.leftPos + 66, this.topPos + 33)
		ModGuiElements.PLUS.blit(guiGraphics, this.leftPos + 29, this.topPos + 35)
		ModGuiElements.DOUGH_MACHINE_ARROW.blit(guiGraphics, this.leftPos + 66, this.topPos + 33)
		guiGraphics.hLine(
			RenderType.gui(),
			this.leftPos + 131,
			this.leftPos + 169,
			this.topPos + 25,
			Color.color(55, 55, 55)
		)
		this.renderSlots(guiGraphics)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.renderEnergyWithTooltip(131, 27, 16, 47, mouseX.toDouble(), mouseY.toDouble())
		guiGraphics.renderFluidWithTooltip(152, 46, 16, 28, mouseX.toDouble(), mouseY.toDouble(), 0)
		guiGraphics.renderFluidWithTooltip(152, 27, 16, 16, mouseX.toDouble(), mouseY.toDouble(), 1)
//		if (this.menu.isCrafting()) ModGuiElements.DOUGH_MACHINE_ARROW_FILLED.drawProgressiveHorizontal(
//			guiGraphics,
//			/*this.menu.scaledProgress,*/ 0, // TODO PROGRESS
//			this.leftPos + 66,
//			this.topPos + 33
//		)
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}