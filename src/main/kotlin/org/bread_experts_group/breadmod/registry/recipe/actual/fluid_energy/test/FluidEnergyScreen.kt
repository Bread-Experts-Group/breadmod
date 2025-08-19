package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadmod.client.gui.screens.BreadModScreen
import org.bread_experts_group.breadmod.client.render.localClient
import org.bread_experts_group.breadmod.client.render.texture.ModGuiElements
import org.bread_experts_group.breadmod.util.Color

class FluidEnergyScreen(
	menu: FluidEnergyMenu,
	title: Component
) : BreadModScreen(menu, title) {
	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos,
			this.topPos,
			this.leftPos + 173,
			this.topPos + 200,
			Color.RED
		)
		guiGraphics.fill(
			RenderType.gui(),
			this.leftPos + 1,
			this.topPos + 1,
			this.leftPos + 172,
			this.topPos + 199,
			Color.GRAY
		)

		guiGraphics.drawString(
			localClient.font,
			/*"progress: ${this.menu.parent.progress}",*/ "progress: TODO",
			this.leftPos + 10,
			this.topPos + 20,
			Color.WHITE,
		)
		guiGraphics.drawString(
			localClient.font,
			/*"max progress: ${this.menu.parent.maxProgress}",*/ "max progress: TODO",
			this.leftPos + 80,
			this.topPos + 20,
			Color.WHITE
		)
		ModGuiElements.DOUGH_MACHINE_ARROW_FILLED.setRotation(-15f)
			.blitScaled(guiGraphics, this.leftPos + 52, this.topPos + 32, 48, 20)
		this.renderSlots(guiGraphics)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)

		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}