package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.WheatCrusherBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu

class WheatCrusherScreen(
	menu: WheatCrusherMenu,
	inventory: Inventory,
	title: Component
) : AbstractModContainerScreen<WheatCrusherMenu, WheatCrusherBlockEntity>(menu, inventory, title) {
	private val texture = modLocation("textures", "gui", "container", "wheat_crusher.png")

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
	private var timer: Int = 20
	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.renderEnergyWithTooltip(151, 14, 16, 47, mouseX.toDouble(), mouseY.toDouble())
		ModTextureLocations.FILLED_VERTICAL_ARROW_9X48.drawProgressiveSpriteVertical(
			guiGraphics,
			this.menu.scaledProgress,
			this.leftPos + 83,
			this.topPos + 33,
			true
		)
		// todo should be updated using [rgMinecraft.gui.guiTicks] for a consistent 20 ticks per second baseline
		if (this.menu.isCrafting()) {
			// Left crushing wheel
			guiGraphics.blit(this.texture, this.leftPos + 51, this.topPos + 38, 176, this.step, 32, 32)
			// Right crushing wheel
			guiGraphics.blit(this.texture, this.leftPos + 92, this.topPos + 38, 208, this.step, 32, 32)
			if (this.timer <= 0) {
				this.timer = 40
				if (this.step < 32) this.step += 32 else this.step = -32
			} else this.timer -= 2
		} else this.step = -32
//        println(menu.parent.progress)
//        println(menu.parent.maxProgress)
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}