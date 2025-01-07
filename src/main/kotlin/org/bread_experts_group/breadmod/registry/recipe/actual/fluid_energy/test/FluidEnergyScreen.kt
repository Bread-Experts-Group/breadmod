package org.bread_experts_group.breadmod.registry.recipe.actual.fluid_energy.test

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderType
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.util.render.localClient
import java.awt.Color

class FluidEnergyScreen(
	menu : FluidEnergyMenu,
	inventory : Inventory,
	title : Component
) : AbstractContainerScreen<FluidEnergyMenu>(menu, inventory, title) {
	override fun renderBg(guiGraphics : GuiGraphics, partialTick : Float, mouseX : Int, mouseY : Int) {
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
			Color(150, 150, 150).rgb
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
	}

	override fun render(guiGraphics : GuiGraphics, mouseX : Int, mouseY : Int, partialTick : Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)

		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}