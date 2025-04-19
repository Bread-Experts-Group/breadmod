package org.bread_experts_group.breadmod.client.gui.screens

import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.client.ModTextureLocations
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu

class DoughMachineScreen(
	menu: DoughMachineMenu,
	inventory: Inventory,
	title: Component
) : AbstractModContainerScreen<DoughMachineMenu, DoughMachineBlockEntity>(menu, inventory, title) {
	private val texture: ResourceLocation = modLocation("textures", "gui", "container", "dough_machine.png")
	private val progressArrow = ModTextureLocations.DOUGH_MACHINE_ARROW
	private val progressArrowFilled = ModTextureLocations.DOUGH_MACHINE_ARROW_FILLED
	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		this.setupRender(this.texture)

		guiGraphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)
		this.progressArrow.blitTexture(guiGraphics, this.leftPos + 66, this.topPos + 33)
		if (this.menu.isCrafting()) this.progressArrowFilled.drawProgressiveSpriteHorizontal(
			guiGraphics,
			this.menu.scaledProgress,
			this.leftPos + 66,
			this.topPos + 33
		)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.renderEnergyWithTooltip(132, 28, 16, 47, mouseX.toDouble(), mouseY.toDouble())
		guiGraphics.renderFluidWithTooltip(153, 47, 16, 28, mouseX.toDouble(), mouseY.toDouble(), 0)
		guiGraphics.renderFluidWithTooltip(153, 28, 16, 16, mouseX.toDouble(), mouseY.toDouble(), 1)
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}
}