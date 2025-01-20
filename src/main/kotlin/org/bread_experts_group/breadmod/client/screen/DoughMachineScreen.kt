package org.bread_experts_group.breadmod.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.ChatFormatting
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.neoforged.neoforge.capabilities.Capabilities
import org.bread_experts_group.breadmod.BreadMod.Companion.modLocation
import org.bread_experts_group.breadmod.BreadMod.Companion.modTranslatable
import org.bread_experts_group.breadmod.client.render.renderFluid
import org.bread_experts_group.breadmod.registry.menu.actual.DoughMachineMenu
import org.bread_experts_group.breadmod.util.formatUnit

class DoughMachineScreen(
	menu: DoughMachineMenu,
	inventory: Inventory,
	title: Component
) : AbstractContainerScreen<DoughMachineMenu>(menu, inventory, title) {
	val texture: ResourceLocation = modLocation("textures", "gui", "container", "dough_machine.png")
	override fun renderBg(guiGraphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
		RenderSystem.setShader(GameRenderer::getRendertypeGuiShader)
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F)
		RenderSystem.setShaderTexture(0, this.texture)

		guiGraphics.blit(this.texture, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight)

		this.renderProgressArrow(guiGraphics)
		this.renderEnergyMeter(guiGraphics)
	}

	override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
		super.render(guiGraphics, mouseX, mouseY, partialTick)
		val showShort = !(this.minecraft ?: return).options.keyShift.isDown
		if (this.isHovering(132, 28, 16, 47, mouseX.toDouble(), mouseY.toDouble())) {
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

		this.menu.parent.level?.getCapability(
			Capabilities.FluidHandler.BLOCK,
			this.menu.parent.blockPos,
			this.menu.parent.blockState.getValue(HorizontalDirectionalBlock.FACING)
		)
			?.let { handler ->
				handler.getFluidInTank(0).let { tank ->
					val fluid = tank.fluid
					if (tank.amount > 0) {
						val percentage = (tank.amount.toFloat() / handler.getTankCapacity(0)) * 28
						guiGraphics.renderFluid(
							x = this.leftPos + 153F,
							y = (this.topPos + 75F),
							width = 16,
							height = percentage.toInt(),
							fluid = fluid,
							flowing = false,
							direction = Direction.SOUTH
						)
					}
				}
			}

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

	private fun renderEnergyMeter(guiGraphics: GuiGraphics) {
		val energyStored = this.menu.energyStoredScaled
		guiGraphics.blit(
			this.texture,
			this.leftPos + 132,
			this.topPos + 28 + 47 - energyStored,
			176,
			64 - energyStored,
			16,
			47
		)
	}
}