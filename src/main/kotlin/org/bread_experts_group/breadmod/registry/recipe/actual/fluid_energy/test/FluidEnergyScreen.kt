package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.client.gui.screens.AbstractRecipeContainerScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import java.awt.Color

class FluidEnergyScreen(
	menu: FluidEnergyMenu,
	inventory: Inventory,
	title: Component
) : AbstractRecipeContainerScreen<FluidEnergyMenu, FluidEnergyBlockEntity>(menu, inventory, title) {
	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos,
			this.topPos,
			this.leftPos + 173,
			this.topPos + 200,
			Color.RED.rgb
		)
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos + 1,
			this.topPos + 1,
			this.leftPos + 172,
			this.topPos + 199,
			Color.GRAY.rgb
		)

		guiGraphics.drawString(
			localClient.font,
			"progress: ${this.menu.parent.progress}",
			this.leftPos + 10,
			this.topPos + 20,
			Color.WHITE.rgb,
		)
		guiGraphics.drawString(
			localClient.font,
			"max progress: ${this.menu.parent.maxProgress}",
			this.leftPos + 80,
			this.topPos + 20,
			Color.WHITE.rgb
		)
		ModGuiElements.DOUGH_MACHINE_ARROW_FILLED.setRotation(-15f).blitScaled(guiGraphics, this.leftPos + 52, this.topPos + 32, 48, 20)
		super.renderBg(guiGraphics, partialTick, mouseX, mouseY)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)

		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}