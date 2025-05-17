package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.client.render.texture.ModTextureLocations
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu

class WheatCrusherScreen(
	menu: WheatCrusherMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeContainerScreen<WheatCrusherMenu, WheatCrusherBlockEntity>(menu, inventory, title) {
	init {
		this.imageWidth = 176
		this.imageHeight = 198
		this.inventoryLabelY = this.imageHeight - 94
	}

	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
//		this.setupRender(this.texture)
//		guiGraphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)
		ModTextureLocations.BACKGROUND.blitScaled(guiGraphics, this.leftPos, this.topPos, 176, 198)
		ModTextureLocations.INVENTORY_SLOTS.blit(guiGraphics, this.leftPos + 7, this.topPos + 115)
		ModTextureLocations.HOTBAR_SLOTS.blit(guiGraphics, this.leftPos + 7, this.topPos + 173)
		ModTextureLocations.SLOT.blit(guiGraphics, this.leftPos + 79, this.topPos + 14)
		ModTextureLocations.RESULT_SLOT.blit(guiGraphics, this.leftPos + 75, this.topPos + 82)
		ModTextureLocations.WHEAT_CRUSHER_ARROW.blit(guiGraphics, this.leftPos + 83, this.topPos + 33)
		ModTextureLocations.WHEAT_CRUSHER_LEFT_WHEEL.let {
			if (this.menu.isCrafting()) it.blit(guiGraphics, this.leftPos + 51, this.topPos + 38)
			else it.blitStaticSprite(guiGraphics, this.leftPos + 51, this.topPos + 38)
		}
		ModTextureLocations.WHEAT_CRUSHER_RIGHT_WHEEL.let {
			if (this.menu.isCrafting()) it.blit(guiGraphics, this.leftPos + 92, this.topPos + 38)
			else it.blitStaticSprite(guiGraphics, this.leftPos + 92, this.topPos + 38)
		}
	}

	private var step: Int = -32
	private var lastTick: Int = 0
	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.renderEnergyWithTooltip(150, 13, 16, 47, mouseX.toDouble(), mouseY.toDouble())
		ModTextureLocations.WHEAT_CRUSHER_ARROW_FILLED.drawProgressiveVertical(
			guiGraphics,
			this.menu.scaledProgress,
			this.leftPos + 83,
			this.topPos + 33,
			true
		)
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}