package org.bread_experts_group.breadmod.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.gui.ModTextureLocations
import org.bread_experts_group.breadmod.registry.menu.actual.WheatCrusherMenu
import org.bread_experts_group.breadmod.util.formatUnit

class WheatCrusherScreen(
	menu: WheatCrusherMenu,
	inventory: Inventory,
	title: Component
) : AbstractContainerScreen<WheatCrusherMenu>(menu, inventory, title) {
	private val texture = modLocation("textures", "gui", "container", "wheat_crusher.png")

	init {
		this.imageWidth = 176
		this.imageHeight = 198
		this.inventoryLabelY = this.imageHeight - 94
	}

	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		RenderSystem.setShader(GameRenderer::getRendertypeGuiShader)
		RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
		RenderSystem.setShaderTexture(0, this.texture)

		guiGraphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)

		ModTextureLocations.ENERGY_METER_16X47.drawProgressiveSpriteVertical(
			guiGraphics,
			this.menu.energyStoredScaled,
			this.leftPos + 151,
			this.topPos + 14,
			false
		)
		ModTextureLocations.VERTICAL_ARROW_9X48.blitTexture(guiGraphics, this.leftPos + 83, this.topPos + 33)
	}

	private var step: Int = -32
	private var timer: Int = 20
	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		val showShort = !(this.minecraft ?: return).options.keyShift.isDown
		if (this.isHovering(151, 14, 16, 47, mouseX.toDouble(), mouseY.toDouble())) {
			this.menu.getEnergyHandler().let {
				guiGraphics.renderComponentTooltip(
					this.font,
					listOf(
						modTranslatable(path = arrayOf("energy"))
							.withStyle(ChatFormatting.RED)
							.withStyle(ChatFormatting.ITALIC),
						Component.literal(
							formatUnit(
								it.energyStored,
								it.maxEnergyStored,
								"FE",
								showShort,
								2
							)
						)
					),
					mouseX, mouseY
				)
			}
		}
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