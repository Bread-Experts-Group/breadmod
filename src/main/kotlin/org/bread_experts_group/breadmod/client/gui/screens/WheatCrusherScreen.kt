package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu

class WheatCrusherScreen(
	menu: WheatCrusherMenu,
	title: Component
) : BreadModScreen(menu, title) {
	init {
		this.imageWidth = 176
		this.imageHeight = 198
		this.inventoryLabelY = this.imageHeight - 94
	}

	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		ModGuiElements.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 176, 198)
		ModGuiElements.WHEAT_CRUSHER_ARROW.blit(guiGraphics, this.leftPos + 83, this.topPos + 33)
		ModGuiElements.WHEAT_CRUSHER_LEFT_WHEEL.let {
//			if (this.menu.isCrafting()) it.blit(guiGraphics, this.leftPos + 51, this.topPos + 38)
//			else it.blitStaticSprite(guiGraphics, this.leftPos + 51, this.topPos + 38)
		}
		ModGuiElements.WHEAT_CRUSHER_RIGHT_WHEEL.let {
//			if (this.menu.isCrafting()) it.blit(guiGraphics, this.leftPos + 92, this.topPos + 38)
//			else it.blitStaticSprite(guiGraphics, this.leftPos + 92, this.topPos + 38)
		}
		this.renderSlots(guiGraphics)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.renderEnergyWithTooltip(150, 13, 16, 47, mouseX.toDouble(), mouseY.toDouble())
//		ModGuiElements.WHEAT_CRUSHER_ARROW_FILLED.drawProgressiveVertical(
//			guiGraphics,
//			this.menu.scaledProgress,
//			this.leftPos + 83,
//			this.topPos + 33,
//			true
//		)
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}