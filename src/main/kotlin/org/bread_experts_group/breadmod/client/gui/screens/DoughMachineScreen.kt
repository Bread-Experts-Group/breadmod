package org.bread_experts_group.breadmod.client.gui.screens

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.registry.block.actual.entity.machine.DoughMachineBlockEntity
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu

class DoughMachineScreen(
	menu: DoughMachineMenu,
	inventory: Inventory,
	title: Component
) : AbstractModContainerScreen<DoughMachineMenu, DoughMachineBlockEntity>(menu, inventory, title) {
	val texture: ResourceLocation = modLocation("textures", "gui", "container", "dough_machine.png")
	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		RenderSystem.setShader(GameRenderer::getRendertypeGuiShader)
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
		RenderSystem.setShaderTexture(0, this.texture)

		guiGraphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)

		this.renderProgressArrow(guiGraphics)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		guiGraphics.renderEnergyWithTooltip(132, 28, 16, 47, mouseX.toDouble(), mouseY.toDouble())
		guiGraphics.renderFluidWithTooltip(153, 47, 16, 28, mouseX.toDouble(), mouseY.toDouble(), 0)
		guiGraphics.renderFluidWithTooltip(153, 28, 16, 16, mouseX.toDouble(), mouseY.toDouble(), 1)
		this.renderTooltip(guiGraphics, mouseX, mouseY)
	}

	private fun renderProgressArrow(guiGraphics: GuiGraphics) {
		if (this.menu.isCrafting()) {
			guiGraphics.blit(
				this.texture,
				this.leftPos + 46,
				this.topPos + 35,
				176,
				0,
				this.menu.scaledProgress,
				17
			)
		}
	}
}