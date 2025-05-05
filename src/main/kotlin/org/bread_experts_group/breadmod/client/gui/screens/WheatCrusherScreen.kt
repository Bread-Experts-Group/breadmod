package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu

class WheatCrusherScreen(
	menu: WheatCrusherMenu,
	inventory: Inventory,
	title: Component
) : AbstractModContainerScreen<WheatCrusherMenu, WheatCrusherBlockEntity>(menu, inventory, title) {
	private val texture: ResourceLocation = modLocation("textures", "gui", "container", "wheat_crusher.png")

	init {
		this.imageWidth = 176
		this.imageHeight = 198
		this.inventoryLabelY = this.imageHeight - 94
	}

	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		this.setupRender(this.texture)

		guiGraphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)
		ModTextureLocations.VERTICAL_ARROW_9X48.blitTexture(guiGraphics, this.leftPos + 83, this.topPos + 33)
	}

	private var step: Int = -32
	private var lastTick: Int = 0
	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		val guiTicks = localClient.gui.guiTicks
		guiGraphics.renderEnergyWithTooltip(151, 14, 16, 47, mouseX.toDouble(), mouseY.toDouble())
		ModTextureLocations.FILLED_VERTICAL_ARROW_9X48.drawProgressiveSpriteVertical(
			guiGraphics,
			this.menu.scaledProgress,
			this.leftPos + 83,
			this.topPos + 33,
			true
		)

		if (this.menu.isCrafting()) {
			if (this.lastTick <= guiTicks) {
				this.lastTick = guiTicks + 8
				if (this.step < 32) this.step += 32 else this.step = -32
			}
			// Left crushing wheel
			guiGraphics.blit(this.texture, this.leftPos + 51, this.topPos + 38, 176, this.step, 32, 32)
			// Right crushing wheel
			guiGraphics.blit(this.texture, this.leftPos + 92, this.topPos + 38, 208, this.step, 32, 32)
		} else this.step = -32
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}